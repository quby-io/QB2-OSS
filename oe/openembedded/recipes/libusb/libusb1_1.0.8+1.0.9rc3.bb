DESCRIPTION = "library to provide userspace access to USB devices"
HOMEPAGE = "http://libusb.sf.net"
SECTION = "libs"
LICENSE = "LGPLv2.1"

COMPATIBLE_MACHINE = "quby2"

PR = "r0"

#SRC_URI = " \
#	${SOURCEFORGE_MIRROR}/libusb/libusb-${PV}.tar.bz2;name=tar \
#	"
#S = "${WORKDIR}/libusb-${PV}"

SRCREV="f07a4a78533b44d124dfe06cbf42afa7fb267359"
#PV = "005+gitr${SRCPV}"
PR = "r0"

#SRC_URI = "git://git.libusb.org/libusb-stuge.git;protocol=http;branch=master;tag=libusb-1.0.9-rc3"
SRC_URI = "git://git.libusb.org/libusb-stuge.git;protocol=git;branch=master"
S = "${WORKDIR}/git"


inherit autotools

EXTRA_OECONF = "--disable-build-docs --enable-debug-log"

#SRC_URI[tar.md5sum] = "37d34e6eaa69a4b645a19ff4ca63ceef"
#SRC_URI[tar.sha256sum] = "21d0d3a5710f7f4211c595102c6b9eccb42435a17a4f5bd2c3f4166ab1badba9"
