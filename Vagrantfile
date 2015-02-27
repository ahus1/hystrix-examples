Vagrant.configure("2") do |config|

  config.vm.box = "ubuntu/trusty32"
  config.vm.network :forwarded_port, host: 4000, guest: 4000
  config.vm.provision :shell,
    :inline => "sudo apt-get -y install build-essential ruby1.9.1-dev nodejs && sudo /usr/bin/gem install jekyll rdiscount --no-ri --no-rdoc"

  if Vagrant.has_plugin?("vagrant-vbguest")
    # https://github.com/dotless-de/vagrant-vbguest
    # set auto_update to false, if you do NOT want to check the correct 
    # additions version when booting this machine
    config.vbguest.auto_update = false

	# do NOT download the iso file from a webserver
    config.vbguest.no_remote = true  
  end

  config.vm.provider "virtualbox" do |v|
    v.name = "hystrix-jekyll"
  #  v.customize ["modifyvm", :id, "--memory", "1024"]
  end
  
end