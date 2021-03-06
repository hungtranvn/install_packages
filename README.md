
# install boost lib for python_c++ interface: fedora

Get the version of Boost that you require. This is for 1.55 but feel free to change or manually download yourself:

wget -O boost_1_55_0.tar.gz https://sourceforge.net/projects/boost/files/boost/1.55.0/boost_1_55_0.tar.gz/download
tar xzvf boost_1_55_0.tar.gz
cd boost_1_55_0/

Get the required libraries, main ones are icu for boost::regex support:

sudo apt-get update
sudo dnf groupinstall "Development Tools" "Development Libraries" \
sudo apt-get install g++ python-devel autotools libicu-devel @development-tools bzip2-devel libboost-devel

Boost's bootstrap setup:

./bootstrap.sh --prefix=/usr/

Then build it with:

./b2

and eventually install it:

sudo ./b2 install

source: https://stackoverflow.com/questions/12578499/how-to-install-boost-on-ubuntu

# install ibus-unikey on fedora
dnf install ibus-unikey
ibus-setup
add in bashrc
export GTK_IM_MODULE=ibus
export XMODIFIERS=@im=ibus
export QT_IM_MODULE=ibus

# Change Docker root dir using systemd

The Docker root dir is usually something like /var/lib/docker by default. Here's how to change it using a systemd .service file.

Find your current root directory using docker info.

$ docker info
    Root Dir: /var/lib/docker/aufs

Since we're using systemd modifying the DOCKER-OPTS tag within /etc/default/docker to include -g /new/root/dir isn't going to work. There are two options, both require you to edit your docker.service file.

    Pro tip: systemctl status docker.service will print the location of this file at the top of the output

Option 1 - Direct edit to docker.service

    Edit ExecStart line to look like this ExecStart =/usr/bin/dockerd -g /new/docker/root/dir -H fd://
    systemctl daemon-reload
    systemctl restart docker
    docker info - verify the root dir has updated

Option 2 - Create a systemd drop-in service file (better way)

This option is preferred as directly editing .service files should be avoided. They may be overwritten during an update for example.

    vi /etc/systemd/system/docker.service.d/docker.root.conf and populate with:

[Service]
ExecStart=
ExecStart=/usr/bin/dockerd -g /new/docker/root -H fd://

    systemctl daemon-reload
    systemctl restart docker
    docker info - verify the root dir has updated

Note - Existing Containers and Images
If you already have containers or images in /var/lib/docker you may wish to stop and back these up before moving them to the new root location. Moving can be done by either rsync -a /var/lib/docker/* /path/to/new/root or if permissions do not matter, you can simply use mv or cp too.

# copy data between sever and local computer
rsync -avzh /home/hungtran/Triqs/cro/opcond hungtran@27.72.102.168:/home/hungtran/cro/opcond

# install packages for python2
python -m pip install --user package_name

# install packages for python3
pip install packageName --user

# install a specific gcc version
Case 1: (https://www.mjmwired.net/resources/mjm-fedora-gcc.html)
Required Files

To install from source obtain GCC from any GCC mirror sites. It is your personal selection in choice of the version of GCC. Upon writing this draft GCC 3.4.4 is available. Try to find a mirror with the full files NOT the diff files. The following is only an example:

gcc-3.4.4.tar.bz2  		26920 KB  	05/19/2005  	10:50:00 AM

gcc-ada-3.4.4.tar.bz2	 	3380 KB 	05/19/2005 	10:50:00 AM
gcc-core-3.4.4.tar.bz2 		12846 KB 	05/19/2005 	10:52:00 AM
gcc-g++-3.4.4.tar.bz2 		2417 KB 	05/19/2005 	10:50:00 AM
gcc-g77-3.4.4.tar.bz2 		882 KB 		05/19/2005 	10:51:00 AM
gcc-java-3.4.4.tar.bz2 		4588 KB 	05/19/2005 	10:51:00 AM
gcc-objc-3.4.4.tar.bz2	 	208 KB 		05/19/2005 	10:51:00 AM
gcc-testsuite-3.4.4.tar.bz2 	2575 KB 	05/19/2005 	10:51:00 AM

Select only which components you wish to install. You will need the gcc-core and any extra language you use (ada, g++, etc.) -OR- download gcc-3.4.4 which will contain ALL of the languages above. ... For this example, I only require gcc-core and gcc-g++.
Setting Up Sources and Directories

GCC recommends you do NOT build in the same directory as your source files are. So simply create a separate build directory. To start, I have downloaded and unzipped my selected tar.bz2 file from above into the directory: gcc.

[mirandam@charon gcc]$ pwd
/home/mirandam/gcc

[mirandam@charon gcc]$ bzip2 -cd gcc-core-3.4.4.tar.bz2 | tar xvf -
[mirandam@charon gcc]$ bzip2 -cd gcc-g++-3.4.4.tar.bz2 | tar xvf -

-OR (the whole thing)-
[mirandam@charon gcc]$ bzip2 -cd gcc-3.4.4.tar.bz2 | tar xvf -

[mirandam@charon gcc]$ mkdir build      (this is our build directory)
[mirandam@charon gcc]$ cd build         (important step)

Our build directory above is /home/mirandam/gcc/build/. All our source code for GCC-3.4.4 is located in /home/mirandam/gcc/gcc-3.4.4/.

download prerequisites:(important)
cd gcc-3.4.4
./contrib/download_prerequisites

Configuring Options
Extensive options are listed on the GCC configuration page provided on gnu.org. Most importantly the options must be set so as to NOT conflict or interfere with the GCC already installed on Fedora Core installation.

The following options are some recommendations and observations taken from Fedora's configuration of GCC.

    prefix is used to set where GCC will be installed. This is typically /usr/local, but I want to keep it completely separate in /opt/gcc34. If I ever plan to remove it, I will simply remove the directory. The compiled gcc,g++,etc. will be in /opt/gcc34/bin/.
    program-suffix is used to attach an extra character or string to every GCC application that gets compiled. I have used 34. So my compiler will be gcc34, g++34, etc. This is done so I avoid any confusion with the existing GCC installed (or even GCC 3.2 in FC4). You can also use program-prefix instead (ex: mygcc).
    enable-languages allows you to control which compilers you want built. If you select all (c,c++,objc,java,f95,ada) make sure you either have all the language added or you used the full gcc tag.bz2 package. (I only care for c and c++.)
    The last set of options (in italics) are meant entirely for compatibility with Fedora.

[mirandam@charon build]$ pwd
/home/mirandam/gcc/build

[mirandam@charon build]$ /home/mirandam/gcc/gcc-3.4.4/configure \
	--prefix=/opt/gcc34 \
	--program-suffix=34 \
	--enable-languages=c,c++ \
	--enable-shared --enable-threads=posix --disable-checking \
	--with-system-zlib --enable-__cxa_atexit --disable-libunwind-exceptions

Compile and Install

Once done configuring above, you just need to compile and install. Everything has been compiled as a user. Make sure to only use when installing.

[mirandam@charon build]$ make             (this compiles, takes 10-30min)

[mirandam@charon build]$ su               (logs you in as root, to install)
Password: 

[root@charon build]# pwd
/home/mirandam/gcc/build

[root@charon build]# make install

Now create a text file named: gcc34.sh and in it add the following.

#!/bin/sh
GCC34_BIN=/opt/gcc34/bin
PATH=$GCC34_BIN:$PATH
export PATH

As root, place the file into /etc/profile.d/ and make sure it has execute permissions for all.

[root@charon ~]# ls -la /etc/profile.d/gcc34.sh 
-rw-r--r--  1 root root 66 Jun 15 21:38 /etc/profile.d/gcc34.sh

[root@charon ~]# chmod 755 /etc/profile.d/gcc34.sh

[root@charon ~]# ls -la /etc/profile.d/gcc34.sh
-rwxr-xr-x  1 root root 66 Jun 15 21:38 /etc/profile.d/gcc34.sh

You should logout and log back in, or just run a new terminal or xterm and gcc34 should work from any prompt.
Using New GCC

If you installed as above, running gcc34 or any other app you installed should be directly accessible. You can verify with the following steps:

[mirandam@charon ~]$ which gcc34
/opt/gcc34/bin/gcc34
[mirandam@charon ~]$ which g++34
/opt/gcc34/bin/g++34

[mirandam@charon ~]$ gcc34 -v
Reading specs from /opt/gcc34/lib/gcc/i686-pc-linux-gnu/3.4.4/specs
Configured with: /home/mirandam/gcc/gcc-3.4.4/configure --prefix=/opt/gcc34
--program-suffix=34 --enable-languages=c,c++ --enable-shared --enable-threads=posix
--disable-checking --with-system-zlib --enable-__cxa_atexit
--disable-libunwind-exceptions
Thread model: posix
gcc version 3.4.4

There are many ways to use an alternate compiler. The 3 methods I commonly use: 1. Environment Variable, 2. Configure Support, and 3. Makefile Support.

Environment Variables
Most softwares support the CC and CXX environment variables. First assign them, then run configure and/or make. Example:

# export CC=gcc34
# export CXX=g++34
# ./configure

Configure Support
If the software is using the standard GNU automake and configure, then there is a chance it supports other compilers by passing in a setting to the configure script. First run configure --help to see if it mentions anything. The following example is from MPlayer:

# ./configure --help
# ./configure --cc=gcc34

Makefile Support
Sometimes the software may just come with a Makefile. Open the Makefile and look inside to see if there are variables that specify the compiler. You can either edit those variables or set them at compile time. For example:

(in Makefile)
	
C_COMPILER = cc
CPLUSPLUS_COMPILER = c++

Then using the Makefile, you can run:

# make CPLUSPLUS_COMPILER=g++34


Case 2:
wget https://ftp.gnu.org/gnu/gcc/gcc-6.3.0/gcc-6.3.0.tar.bz2
tar xvf gcc-6.3.0.tar.gz
cd gcc-6.3.0
apt build-dep gcc
./contrib/download_prerequisites
cd ..
mkdir objdir
cd objdir
$PWD/../gcc-6.3.0/configure --prefix=/usr/bin/gcc-6.3 --enable-languages=c,c++,fortran,go --disable-multilib
make -j 8
make install

# To switch between gcc6 and gcc7
update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-7 1 --slave /usr/bin/g++ g++ /usr/bin/g++-7
update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-6 2 --slave /usr/bin/g++ g++ /usr/bin/g++-6
update-alternatives --config gcc
