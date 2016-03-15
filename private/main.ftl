<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="/img/favicon.ico">

    <title>Photo Gallery</title>

    <!-- Bootstrap core CSS -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="/css/style.css" rel="stylesheet">
  </head>
  
<script>
function redirect(url) {
	window.location.assign(url);
}
</script>

<body>
<div class="jumbotron">
	<div class="container">
		<h1>My Photo Gallery!</h1>
		
		<p>You can see my photos here! You can also post your photos!</p>
		
		<form action="/photoupload" method="post" enctype="multipart/form-data">
			<fieldset class="form-group">
			
			<div class="row">
				<div class="col-md-4">
					<label for="label">Photo Label</label>
					<input id="label" type="text" class="form-control" name="label" placeholder="My favourite landscape photo">
				</div>
				<div class="col-md-4">
					<label for="date">Photo Date</label>
					<input id="date" type="text" class="form-control" name="date" value="${today!}" placeholder="dd/mm/yyyy">
				</div>
			</div>
			<div class="row">
				<div class="col-md-4">
					<label for="photo">Photo File</label>
					<input id="photo" class="form-control" type="file" name="photo">
				</div>
			</div>
			<div class="row">
				<div class="col-md-4">
					<input class="form-control" type="submit" name="submit" value="Upload">
				</div>
			</div>
			</fieldset>
		</form>
	</div>
</div>
    
<section id="photos">
	<div class="container">
		<!-- Example row of columns -->
	  	<#if dates?? && (dates?size > 0)>
			<#list dates as date>
		  		<div class="row">
		  			<div class="col-md-12"><h2>${date!}</h2></div>
		  		</div>
				<div class="row">
					<#if photos?api.get(date)??>
						<#list photos?api.get(date) as photo>
							<div class="col-md-3 download photo-box" onclick="redirect('${photo.path}')">
								<div style="border: 1px #c0c0c0 solid">
									<img src="${photo.path}" width="150" height="200" border=0>
									<div class="photo-label">${photo.label!}</div><br/>
								</div>
							</div>
						</#list>
					</#if>
				</div>
			</#list>
		</#if>
	</div> <!-- /container -->
</section>

<footer>
	<div class="container">
		<hr/>
		
		<p>&copy; 2015 Company, Inc.</p>
	</div>
</footer>


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <!-- Placed at the end of the document so the pages load faster -->
    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>
  </body>
</html>