
ARCH="`pwd | xargs basename`"
OEBASE="`pwd | xargs dirname`"
echo "ARCH=$ARCH"

# add bitbake to path?
echo $PATH | grep -q bitbake/bin || 
	export PATH="$OEBASE/bitbake/bin:$PATH"
# add Python?
echo $PATH | grep -q Python ||
	PATH="$OEBASE/Python-2.6.6:$PATH"
#echo "PATH=$PATH"

# let bitbake know where to find .conf files
BBPATH="$OEBASE/$ARCH:$OEBASE/homeautomationeurope:$OEBASE/openembedded"
echo "BBPATH=$BBPATH"

export PATH BBPATH OEBASE

# allow BitBake to inherit the $OEBASE variable from the environment,
export BB_ENV_EXTRAWHITE="OEBASE"

echo ""

echo "`bitbake --version`, `python --version 2>&1`"

echo ""



