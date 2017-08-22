# change version number and hash code as shown on Apache download page
# http://jmeter.apache.org/download_jmeter.cgi
$version="2.11"
$masterhash = "14b6dfc04f912e45b482e4563fdf1c3a"

$url = "https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-$version.zip"

$scriptPath = split-path -parent $MyInvocation.MyCommand.Definition

# see if folder has been created
If (-not (Test-Path "$scriptPath/apache-jmeter-$version")) {

  # see if ZIP file exists
  If (-not (Test-Path "$scriptPath/apache-jmeter-$version.zip")) {
    "downloading installation archive from $url"
    $webclient = New-Object System.Net.WebClient
    $webclient.DownloadFile($url,"$scriptPath/apache-jmeter-$version.zip")
  }

  # create a md5 hash code from ZIP file and normalize it
  "verifying hash code"
  $md5 = new-object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider
  $hash = [System.BitConverter]::ToString($md5.ComputeHash([System.IO.File]::ReadAllBytes("apache-jmeter-$version.zip")))
  $hash = $hash.replace("-","").toLower()

  # check if hash codes match
  If ($hash -ne $masterhash) {
    "Hash of master file $masterhash doesn't match downloaded file $hash. ZIP file is possibly corrupted"    
    exit
  } 

  "install jmeter"
  $shell_app=new-object -com shell.application
  $zip_file = $shell_app.namespace("$scriptPath\apache-jmeter-$version.zip")
  $destination = $shell_app.namespace($scriptPath)
  $destination.Copyhere($zip_file.items())

}


"starting jmeter"
start-process $scriptPath/apache-jmeter-$version/bin/jmeter.bat -ArgumentList $args -NoNewWindow -Wait  

