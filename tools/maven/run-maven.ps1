# change version number and hash code as shown on Apache download page
# http://maven.apache.org/download.cgi
$version="3.2.5"
$masterhash = "e617600b75dff1f69b67e92d55f47c6a"

$url = "http://www.eu.apache.org/dist/maven/maven-3/$version/binaries/apache-maven-$version-bin.zip"

$scriptPath = split-path -parent $MyInvocation.MyCommand.Definition

# see if folder has been created
If (-not (Test-Path "$scriptPath/apache-maven-$version")) {

  # see if ZIP file exists
  If (-not (Test-Path "$scriptPath/apache-maven-$version.zip")) {
    "downloading installation archive from $url"
    $webclient = New-Object System.Net.WebClient
    $webclient.DownloadFile($url,"$scriptPath/apache-maven-$version.zip")
  }

  # create a md5 hash code from ZIP file and normalize it
  "verifying hash code"
  $md5 = new-object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider
  $hash = [System.BitConverter]::ToString($md5.ComputeHash([System.IO.File]::ReadAllBytes("$scriptPath/apache-maven-$version.zip")))
  $hash = $hash.replace("-","").toLower()

  # check if hash codes match
  If ($hash -ne $masterhash) {
    "Hash of master file $masterhash doesn't match downloaded file $hash. ZIP file is possibly corrupted"    
    exit
  } 

  "install maven"
  $shell_app=new-object -com shell.application
  $zip_file = $shell_app.namespace("$scriptPath\apache-maven-$version.zip")
  $destination = $shell_app.namespace($scriptPath)
  $destination.Copyhere($zip_file.items())

  "configuring maven"
  # patch m2 folder
  $xml = [xml](Get-Content "$scriptPath/apache-maven-$version/conf/settings.xml")
  $child = $xml.CreateElement("localRepository")
  $scriptPathMaven = $scriptPath.replace("\","/")
  $child.set_InnerXml("$scriptPath/m2")
  $child = $xml.settings.AppendChild($child) 
  $xml.Save("$scriptPath/apache-maven-$version/conf/settings.xml")
  
}

"starting maven"
start-process $scriptPath/apache-maven-$version/bin/mvn.bat -ArgumentList $args -NoNewWindow -Wait  

