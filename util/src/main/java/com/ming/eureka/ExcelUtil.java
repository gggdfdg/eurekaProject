/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka;

import com.google.common.collect.Lists;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;


/**
 * @author lxm 2016年2月24日
 */
public class ExcelUtil {

    /**
     * 生成文档
     * @param response 回复
     * @param xlsName sheet 提示词
     * @param dataObj 对象列表
     * @param lineHeadHint xls行属性名
     * @param headProperty dataObj的属性名
     * @param targetFileName 保存的文件名
     * @param <T>
     * @throws Exception
     */
    public static <T> void exportXlstoResponse(HttpServletResponse response, String xlsName, List<T> dataObj, List<String> lineHeadHint, List<String> headProperty, String targetFileName) throws Exception {
        XSSFWorkbook wb = ExcelUtil.list2XSS(xlsName, dataObj,
                lineHeadHint, headProperty);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment;filename="
                + new String((targetFileName + ".xlsx").getBytes("utf-8"), "ISO8859-1"));
        try {
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static XSSFWorkbook list2XSS(String sheetName, List list, List<String> titles, List<String> paramNames) {

        Asserts.notNull(list, "list can not null");
        Asserts.notNull(titles, "titles can not null");
        Asserts.notNull(paramNames, "paramNames can not null");
        Asserts.check(titles.size() == paramNames.size(), "size not eq");

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet(sheetName);
        XSSFRow row = sheet.createRow((int) 0);
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        int i = 0;
        for (String title : titles) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(title);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
            i++;
        }

        Object value;
        if (CollectionUtils.isNotEmpty(list)) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                Object obj = list.get(i);

                int j = 0;
                for (String paramName : paramNames) {
                    value = Reflections.getFieldValue(obj, paramName);
                    if (value == null) {
                        value = "";
                    }
                    row.createCell(j).setCellValue(String.valueOf(value));
                    j++;
                }

            }
        }
        return wb;
    }
    public static <T>  List<T>  xlsToList(InputStream in, String sheetName, Class<T> entityClass,
                                          LinkedHashMap<String, String> fieldMap, LinkedHashMap<String, String> canNilfieldMap, String[] uniqueFields) throws Exception {
        return xlsToList(in, sheetName, entityClass, fieldMap, canNilfieldMap, uniqueFields, null);
    }

    /**
     * excel转list
     * @param in  excel文件流
     * @param sheetName 要导入的表名
     * @param entityClass 要导入的实体
     * @param fieldMap 表头与类字段映射
     * @param uniqueFields 指定业务主键组合（即复合主键），这些列的组合不能重复, 没有的话传null
     * @param canNilfieldMap 可以为空的表头与类字段映射
     * @return
     * @throws Exception
     */
    public static <T>  List<T>  xlsToList(InputStream in, String sheetName, Class<T> entityClass,
                                          LinkedHashMap<String, String> fieldMap, LinkedHashMap<String, String> canNilfieldMap, String[] uniqueFields, Map<String, Object> defaultValueMap) throws Exception {

        // 定义要返回的list
        List<T> resultList=new ArrayList<T>();
        try {
            Workbook wb = Workbook.getWorkbook(in);
            Sheet sheet = null;
            if (sheetName.trim().length() != 0)
                sheet = wb.getSheet(sheetName);
            else
                sheet = wb.getSheet(0);

            //获取工作表的有效行数
            int realRows = 0;
            for (int i = 0; i < sheet.getRows(); i++) {
                int nullCols = 0;
                for (int j = 0; j < sheet.getColumns(); j++) {
                    Cell currentCell = sheet.getCell(j, i);
                    if (currentCell == null || "".equals(currentCell.getContents().toString())) {
                        nullCols++;
                    }
                }
                if (nullCols == sheet.getColumns()) {
                    continue;
                } else {
                    realRows++;
                }
            }

            //如果Excel中没有数据则提示错误
            if(realRows<=1){
                throw new Exception("Excel文件中没有任何数据");
            }

            Cell[] firstRow=sheet.getRow(0);

            String[] excelFieldNames=new String[firstRow.length];

            //获取Excel中的列名
            for(int i=0;i<firstRow.length;i++){
                excelFieldNames[i]=firstRow[i].getContents().toString().trim();
            }

            //判断需要的字段在Excel中是否都存在
            boolean isExist=true;
            List<String> excelFieldList = Arrays.asList(excelFieldNames);
            String columnName = "";
            for(String cnName : fieldMap.keySet()){
                if(!excelFieldList.contains(cnName)){
                    columnName = cnName;
                    isExist=false;
                    break;
                }
            }

            //如果有列名不存在，则抛出异常，提示错误
            if(!isExist){
                throw new Exception("Excel中缺少必要的“"+columnName+"”列，或“"+columnName+"”列名称有误");
            }

            //将列名和列号放入Map中,这样通过列名就可以拿到列号
            LinkedHashMap<String, Integer> colMap=new LinkedHashMap<String, Integer>();
            for(int i=0;i<excelFieldNames.length;i++){
                colMap.put(excelFieldNames[i], firstRow[i].getColumn());
            }

            if (uniqueFields != null && uniqueFields.length != 0) {
                //判断是否有重复行
                //1.获取uniqueFields指定的列
                Cell[][] uniqueCells=new Cell[uniqueFields.length][];
                for(int i=0;i<uniqueFields.length;i++){
                    int col=colMap.get(uniqueFields[i]);
                    uniqueCells[i]=sheet.getColumn(col);
                }
                //2.从指定列中寻找重复行
                for(int i=1;i<sheet.getRows();i++){
                    int nullCols=0;
                    for(int j=0;j<uniqueFields.length;j++){
                        String currentContent=uniqueCells[j][i].getContents();
                        Cell sameCell=sheet.findCell(currentContent,
                                uniqueCells[j][i].getColumn(),
                                uniqueCells[j][i].getRow()+1,
                                uniqueCells[j][i].getColumn(),
                                uniqueCells[j][realRows-1].getRow(),
                                true);
                        if(sameCell!=null){
                            nullCols++;
                        }
                    }

                    if(nullCols==uniqueFields.length){
                        throw new Exception("Excel中有重复行，请检查");
                    }
                }
            }

            //将sheet转换为list
            for(int i=1;i<sheet.getRows();i++){
                //新建要转换的对象
                Cell[] cells = sheet.getRow(i);
                boolean nullRow = true;
                for (Cell cc : cells) {
                    if (cc.getType() != CellType.EMPTY) {
                        nullRow = false;
                        break;
                    }
                }
                if (nullRow) {
                    continue;
                }
                T entity = entityClass.newInstance();
                // 设置默认值
                if (defaultValueMap != null) {
                    for (Entry<String, Object> entry : defaultValueMap.entrySet()) {
                        setFieldValueByName(entry.getKey(), entry.getValue(), entity);
                    }
                }
                //给对象中的字段赋值(必须)
                for (Entry<String, String> entry : fieldMap.entrySet()){
                    //获取中文字段名
                    String cnNormalName=entry.getKey();
                    //获取英文字段名
                    String enNormalName=entry.getValue();
                    //根据中文字段名获取列号
                    int col=colMap.get(cnNormalName);

                    //获取当前单元格中的内容
                    String content=sheet.getCell(col, i).getContents().toString().trim();

                    //给对象赋值
                    setFieldValueByName(enNormalName, content, entity);
                }
                if (canNilfieldMap != null) {
                    for (Entry<String, String> entry : canNilfieldMap.entrySet()) {
                        //获取中文字段名
                        String cnNormalName=entry.getKey();
                        //获取英文字段名
                        String enNormalName=entry.getValue();
                        //根据中文字段名获取列号
                        if (!colMap.containsKey(cnNormalName)) continue;
                        int col = colMap.get(cnNormalName);
                        //获取当前单元格中的内容
                        String content=sheet.getCell(col, i).getContents().toString().trim();

                        //给对象赋值
                        setFieldValueByName(enNormalName, content, entity);
                    }
                }
                resultList.add(entity);
            }
        } catch(Exception e){
            // e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return resultList;
    }

    public static <T>  List<T>  xlsxToList(InputStream in, String sheetName, Class<T> entityClass,
                                           LinkedHashMap<String, String> fieldMap, LinkedHashMap<String, String> canNilfieldMap, String[] uniqueFields) throws Exception {
        return xlsxToList(in, sheetName, entityClass, fieldMap, canNilfieldMap, uniqueFields, null);
    }

    /**
     * excel转list
     * @param in  excel文件流
     * @param sheetName 要导入的表名
     * @param entityClass 要导入的实体
     * @param fieldMap 表头与类字段映射
     * @param uniqueFields 指定业务主键组合（即复合主键），这些列的组合不能重复, 没有的话传null
     * @param canNilfieldMap 可以为空的表头与类字段映射
     * @return
     * @throws Exception
     */
    public static <T>  List<T>  xlsxToList(InputStream in, String sheetName, Class<T> entityClass,
                                           LinkedHashMap<String, String> fieldMap, LinkedHashMap<String, String> canNilfieldMap, String[] uniqueFields, Map<String, Object> defaultValueMap) throws Exception {

        // 定义要返回的list
        List<T> resultList=new ArrayList<T>();

        try {
            //获取工作表
            XSSFSheet sheet = null;
            XSSFWorkbook wb = new XSSFWorkbook(in);
            if (sheetName.trim().length() != 0)
                sheet = wb.getSheet(sheetName);
            else{
                sheet = wb.getSheetAt(0);
            }

            int totalrows = sheet.getPhysicalNumberOfRows();// --获取sheet总行数
            //如果Excel中没有数据则提示错误
            if(totalrows<=1){
                throw new Exception("Excel文件中没有任何数据");
            }
            XSSFRow firstRow=sheet.getRow(0);
            String[] excelFieldNames=new String[firstRow.getPhysicalNumberOfCells()];

            //获取Excel中的列名
            for(int i=0;i<firstRow.getPhysicalNumberOfCells();i++){
                XSSFCell cell = firstRow.getCell(i);
                excelFieldNames[i]=cell.getStringCellValue();
            }

            //判断需要的字段在Excel中是否都存在
            boolean isExist=true;
            List<String> excelFieldList = Arrays.asList(excelFieldNames);
            String columnName = "";
            for(String cnName : fieldMap.keySet()){
                if(!excelFieldList.contains(cnName)){
                    columnName = cnName;
                    isExist=false;
                    break;
                }
            }

            //如果有列名不存在，则抛出异常，提示错误
            if(!isExist){
                throw new Exception("Excel中缺少必要的“"+columnName+"”列，或“"+columnName+"”列名称有误");
            }

            //将列名和列号放入Map中,这样通过列名就可以拿到列号
            LinkedHashMap<String, Integer> colMap=new LinkedHashMap<String, Integer>();
            for(int i=0;i<excelFieldNames.length;i++){
                colMap.put(excelFieldNames[i], firstRow.getCell(i).getColumnIndex());
            }
            //将sheet转换为list
            for(int i=1;i<=sheet.getLastRowNum();i++){
                //新建要转换的对象
                //获取当前单元格中的内容
                if (sheet.getRow(i) == null) {
                    continue;
                }
                T entity=entityClass.newInstance();
                // 设置默认值
                if (defaultValueMap != null) {
                    for (Entry<String, Object> entry : defaultValueMap.entrySet()) {
                        setFieldValueByName(entry.getKey(), entry.getValue(), entity);
                    }
                }
                //给对象中的字段赋值(必须)
                for (Entry<String, String> entry : fieldMap.entrySet()){
                    //获取中文字段名
                    String cnNormalName=entry.getKey();
                    //获取英文字段名
                    String enNormalName=entry.getValue();
                    //根据中文字段名获取列号
                    int col=colMap.get(cnNormalName);

                    XSSFCell cell = sheet.getRow(i).getCell(col);
                    if (cell != null) {
                        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String content=cell.getStringCellValue().trim();
                        //给对象赋值
                        setFieldValueByName(enNormalName, content, entity);
                    } else {
                        setFieldValueByName(enNormalName, "", entity);
                    }
                }
                if (canNilfieldMap != null) {
                    for (Entry<String, String> entry : canNilfieldMap.entrySet()) {
                        //获取中文字段名
                        String cnNormalName=entry.getKey();
                        //获取英文字段名
                        String enNormalName=entry.getValue();
                        //根据中文字段名获取列号
                        if (!colMap.containsKey(cnNormalName)) continue;
                        int col = colMap.get(cnNormalName);
                        //获取当前单元格中的内容
                        XSSFCell cell = sheet.getRow(i).getCell(col);
                        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                        String content=cell.getStringCellValue().trim();
                        //给对象赋值
                        setFieldValueByName(enNormalName, content, entity);
                    }
                }

                resultList.add(entity);
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return resultList;
    }

    /**
     * List转excel
     * @param dir 要保存的目录
     * @param fileName 文件名
     * @param src 数据源
     * @param fieldMap 字段-表头映射
     * @param mergeColumn 需要合并单元格的列索引,从0开始
     * @param lockColumn 要锁定的列
     * @return
     * @throws Exception
     */
    public static <T> String list2Excel(String dir, String fileName, List<T> src,
                                        LinkedHashMap<String, String> fieldMap,
                                        List<Integer> mergeColumn, List<ExcelHead> headMap, List<Integer> lockColumn) throws Exception {
        int index = 0, rowCount = 0;
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet1");
        // 表头样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        cellStyle.setFont(font);//选择需要用到的字体格式
        // 表头
        HSSFRow head = null;
        if (headMap != null && headMap.size() != 0) {
            head = sheet.createRow(rowCount++);
            for (ExcelHead h : headMap) {
                for (int col = 0; col < h.getOffset(); col++) {
                    HSSFCell cell = head.createCell(index++);
                    cell.setCellValue("");
                }
                HSSFCell cell = head.createCell(index++);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(h.getName());
                int firstcol = index-1;
                int lastcol = index-1+h.getColspan()-1;
                if(firstcol>lastcol){
                    lastcol = firstcol;
                }
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, firstcol, lastcol));
                index = index - 1 + h.getColspan();
            }
        }
        index = 0;
        head = sheet.createRow(rowCount++);
        for (String fieldName : fieldMap.keySet()) {
            HSSFCell cell = head.createCell(index++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(fieldMap.get(fieldName));
        }

        for (T obj : src) {
            index = 0;
            HSSFRow row = sheet.createRow(rowCount++);
            for (String fieldName : fieldMap.keySet()) {
                HSSFCell cell = row.createCell(index++);
                Object objValue = getFieldValueByNameSequence(fieldName, obj);
                String fieldValue = objValue == null ? "" : objValue.toString();
                HSSFCellStyle style= wb.createCellStyle();
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
                style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
                if (fieldValue.indexOf("\n") >= 0) {
                    style.setWrapText(true);
                }
                cell.setCellStyle(style);
                if (fieldName.equals("host") || fieldName.equals("guest")) {
                    Object willRed = getFieldValueByNameSequence("red", obj);
                    String willRedValue = willRed == null ? "" : willRed.toString();
                    if (willRedValue.equals(fieldName)) {
                        HSSFRichTextString ts= new HSSFRichTextString(fieldValue);
                        HSSFFont font1 = wb.createFont();
                        font1.setColor(HSSFColor.RED.index);
                        ts.applyFont(fieldValue.indexOf("\n")+1, ts.length(), font1);
                        cell.setCellValue(ts);
                    } else {
                        cell.setCellValue(fieldValue);
                    }
                } else {
                    cell.setCellValue(fieldValue);
                }
            }
        }
        int addoffset = 1;
        if (headMap != null && headMap.size() != 0) {
            setColumnAutoSize(sheet, fieldMap.size(), true);
            addoffset = 2;
        } else {
            setColumnAutoSize(sheet, fieldMap.size(), false);
        }

        if (mergeColumn != null) {
            for (Integer columnIndex : mergeColumn) {
                if (columnIndex.intValue() >= fieldMap.size()) continue;
                mergeColumn(1, src.size()+addoffset, sheet, columnIndex);
            }
        }
        if (lockColumn != null) {
            for (Integer columnIndex : lockColumn) {
                if (columnIndex.intValue() >= fieldMap.size()) continue;
                lockColumn(1, src.size()+addoffset, sheet, wb, columnIndex);
            }
        }
        // 写入文件
        //生成绝对路径（并创建好相关目录）
        String descPath = FileUtil.generateAndCreateHashPath(dir, fileName, ".xls");

        File xls = new File(descPath);
        if (xls.exists())
            xls.delete();
        FileOutputStream ouputStream = new FileOutputStream(xls);
        wb.write(ouputStream);

        ouputStream.flush();
        ouputStream.close();
        wb.close();
        return descPath;
    }



    private static boolean existsField(Class clz,String fieldName){
        try{
            return clz.getDeclaredField(fieldName)!=null;
        }
        catch(Exception e){
        }
        if(clz!=Object.class){
            return existsField(clz.getSuperclass(),fieldName);
        }
        return false;
    }

    private static Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception {
        Object value = null;

        if (o instanceof HashMap) {
            return ((HashMap) o).get(fieldNameSequence);
        }

        //将fieldNameSequence进行拆分 
        String[] attributes = fieldNameSequence.split("\\.");
        if (attributes.length==1) {
            value = getFieldValueByName(fieldNameSequence, o);
        } else {
            //根据属性名获取属性对象 
            Object fieldObj=getFieldValueByName(attributes[0], o);
            String subFieldNameSequence=fieldNameSequence.substring(fieldNameSequence.indexOf(".")+1);
            value=getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;
    }


    private static Object getFieldValueByName(String fieldName, Object o) throws Exception {
        Object value=null;
        Field field = getFieldByName(fieldName, o.getClass());
        if(field !=null){
            field.setAccessible(true);
            value=field.get(o);
        }else{
            return null;
        }
        return value;
    }

    private static Field getFieldByName(String fieldName, Class<?> clazz) {
        //拿到本类的所有字段 
        Field[] selfFields=clazz.getDeclaredFields();
        //如果本类中存在该字段，则返回 
        for (Field field : selfFields){
            if (field.getName().equals(fieldName)){
                return field;
            }
        }

        //否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }
        //如果本类和父类都没有，则返回空
        return null;
    }

    private static void setColumnAutoSize(HSSFSheet sheet, int cols, boolean hasTwoHead){
        //获取本列的最宽单元格的宽度
        for(int i = 0;i < cols; i++) {
            int colWith=0;
            for(int j = hasTwoHead ? 1 : 0; j <= sheet.getLastRowNum(); j++) {
                if (sheet.getRow(j) == null) break;
                String content = sheet.getRow(j).getCell(i).getStringCellValue();
                if (content.indexOf("\n") >= 0) {
                    String[] tmpArray = content.split("\n");
                    String index = "";
                    for (String str : tmpArray) {
                        if (str.length() > index.length()) {
                            index = str;
                        }
                    }
                    content = index;
                }
                int cellWith = content.getBytes().length;
                if(colWith < cellWith){
                    colWith = cellWith;
                }
            }
            sheet.setColumnWidth(i, colWith*256);
        }
    }

    private static void lockColumn(int fromRowIndex, int totalRow, HSSFSheet sheet, HSSFWorkbook wb, int column) {
        int currnetRow = fromRowIndex;//开始查找的行
        for (int p = currnetRow; p < totalRow; p++) { //totalRow 总行数
            HSSFCell currentCell = sheet.getRow(p).getCell(column);
            HSSFCellStyle style = wb.createCellStyle();
            style.setLocked(true);
            currentCell.setCellStyle(style);
        }
    }

    private static void mergeColumn(int fromRowIndex, int totalRow, HSSFSheet sheet, int column) {
        int currnetRow = fromRowIndex;//开始查找的行
        for (int p = currnetRow; p < totalRow; p++) { //totalRow 总行数
            HSSFCell currentCell = sheet.getRow(p).getCell(column);
            String current = currentCell.getStringCellValue();
            HSSFCell nextCell = null;
            String next = "";
            if (p < totalRow+1){
                HSSFRow nowRow = sheet.getRow(p+1);
                if (nowRow != null) {
                    nextCell = nowRow.getCell(column);
                    next = nextCell.getStringCellValue();
                } else {
                    next = "";
                }
            } else {
                next = "";
            }

            if (current.equals(next)){//比对是否相同
                currentCell.setCellValue("");
                continue;
            } else {
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currnetRow, p, column, column));//合并单元格
                HSSFCell nowCell = sheet.getRow(currnetRow).getCell(column);
                nowCell.setCellValue(current);
                currnetRow = p + 1;
            }
        }
    }

    private static void setFieldValueByName(String fieldName,Object fieldValue,Object o) throws Exception{

        Field field=getFieldByName(fieldName, o.getClass());
        if(field!=null){
            field.setAccessible(true);
            //获取字段类型
            Class<?> fieldType = field.getType();

            //根据字段类型给字段赋值
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType)
                    || (Integer.class == fieldType)) {
                if (fieldName.equals("enterTime")) {
                    field.set(o, (int)(60*Double.parseDouble(fieldValue.toString())));
                } else {
                    if(StringUtils.isNumeric(fieldValue.toString())){
                        field.set(o, Integer.parseInt(fieldValue.toString()));
                    } else {
                        field.set(o, 0);
                    }
                }
            } else if ((Long.TYPE == fieldType)
                    || (Long.class == fieldType)) {
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if ((Float.TYPE == fieldType)
                    || (Float.class == fieldType)) {
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if ((Short.TYPE == fieldType)
                    || (Short.class == fieldType)) {
                field.set(o, Short.valueOf(fieldValue.toString()));
            } else if ((Double.TYPE == fieldType)
                    || (Double.class == fieldType)) {
                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Character.TYPE == fieldType) {
                if ((fieldValue!= null) && (fieldValue.toString().length() > 0)) {
                    field.set(o, Character
                            .valueOf(fieldValue.toString().charAt(0)));
                }
            }else if(Date.class==fieldType){
                Date date = DateUtil.StringToUtilDate(fieldValue.toString()); // yyyy-MM-dd HH:mm:ss
                if (date == null) {
                    date = DateUtil.StringToUtilDate2(fieldValue.toString()); // yyyy-MM-dd
                }
                if (date == null) {
                    date = DateUtil.StringToUtilDate(fieldValue.toString(), "yyyy/MM/dd");
                }

                field.set(o, date);
            }else{
                field.set(o, fieldValue);
            }
        }else{
            throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
        }
    }

    public static void main(String[] args) {
        List<zz> test = new ArrayList<>();
        test.add(zz.fsf("445464", "阿肥发"));
        test.add(zz.fsf("0000", "主牌1"));
        test.add(zz.fsf("7888", "猪排1"));
        XSSFWorkbook wb = ExcelUtil.list2XSS("IMAccount", test,
                Lists.newArrayList("账号", "密码"), Lists.newArrayList("username", "password"));
        System.out.println(wb);
    }

    public static class zz {
        public String username;
        public String password;

        public static zz fsf(String password, String username) {
            zz zt = new zz();
            zt.password = password;
            zt.username = username;
            return zt;
        }

    }
}
