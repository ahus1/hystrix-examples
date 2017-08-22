# change version number and hash code as shown on Apache download page
# http://tomcat.apache.org/download-80.cgi
$version="8.0.45"
$masterhash = "ffa9b438c8b74d03aa6fd5255e80bbc9"

$url = "http://www.eu.apache.org/dist/tomcat/tomcat-8/v$version/bin/apache-tomcat-$version.zip"

# see if folder has been created
If (-not (Test-Path "apache-tomcat-$version")) {

  # see if ZIP file exists
  If (-not (Test-Path "apache-tomcat-$version.zip")) {
    "downloading installation archive from $url"
#   proxy setting not needed, will do this automatically
#    $proxy = [System.Net.WebRequest]::GetSystemWebProxy()
#    $proxy.Credentials = [System.Net.CredentialCache]::DefaultCredentials    #
	$webclient = New-Object System.Net.WebClient
#	$webclient.proxy = $proxy
    $webclient.DownloadFile($url,"apache-tomcat-$version.zip")
  }

  # create a md5 hash code from ZIP file and normalize it
  "verifying hash code"
  $md5 = new-object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider
  $hash = [System.BitConverter]::ToString($md5.ComputeHash([System.IO.File]::ReadAllBytes("apache-tomcat-$version.zip")))
  $hash = $hash.replace("-","").toLower()

  # check if hash codes match
  If ($hash -ne $masterhash) {
    "Hash of master file $masterhash doesn't match downloaded file $hash. ZIP file is possibly corrupted"    
    exit
  } 

  "install tomcat"
  $shell_app=new-object -com shell.application
  (Get-Location).Path + "\apache-tomcat-$version.zip"
  $zip_file = $shell_app.namespace((Get-Location).Path + "\apache-tomcat-$version.zip")
  (Get-Location).Path
  $destination = $shell_app.namespace((Get-Location).Path)
  $destination.Copyhere($zip_file.items())
}

"configuring tomcat"
# setup parameters used for hystrix and remote debugging
copy files/setenv.bat "apache-tomcat-$version/bin"

# patch tomcat users
$xml = [xml](Get-Content ./apache-tomcat-$version/conf/tomcat-users.xml)
$xml.'tomcat-users'.set_InnerXML("<role rolename=""manager-gui""/><role rolename=""manager-script""/><user username=""deploy"" password=""deploy"" roles=""manager-script""/><user username=""tomcat"" password=""tomcat"" roles=""manager-gui""/>")
$xml.Save("./apache-tomcat-$version/conf/tomcat-users.xml")

# patch context
$xml = [xml](Get-Content ./apache-tomcat-$version/conf/context.xml)
$xml.Context.SetAttribute("antiResourceLocking", "true") 
$xml.Save("./apache-tomcat-$version/conf/context.xml")

"starting tomcat"
cp "files/index.jsp" "apache-tomcat-$version/webapps/root"
cd "apache-tomcat-$version/bin"
start-process startup.bat

