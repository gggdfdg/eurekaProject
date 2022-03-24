<html>
	<head>
		<meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">	
	    <title>404 Error</title>	
	    <link href="${static('/css/bootstrap.min.css')}" rel="stylesheet">
	    <link href="${static('/css/font-awesome/css/font-awesome.css')}" rel="stylesheet">	
	    <link href="${static('/css/animate.css')}" rel="stylesheet">
	    <link href="${static('/css/style.css')}" rel="stylesheet">	
	</head>
	<body class="gray-bg">
		<div class="middle-box text-center animated fadeInDown">
	        <h1>404</h1>
	        <h3 class="font-bold">页面未找到</h3>
	
	        <div class="error-desc">
	        	对不起, 您没有权限访问该页面.
	        	<br/><a href="${ctx}/user" class="btn btn-primary m-t">首页</a>
	        </div>
	        <a>
	    </div>

		<!-- Mainly scripts -->
    	<script src="${static('/js/jquery-2.1.1.js')}"></script>
    	<script src="${static('/js/bootstrap.min.js')}"></script>
	</body>
</html>
