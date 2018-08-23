DESCRIPTION = "Library for reading/writing modbus"
HOMEPAGE = "http://libmodbus.org/"
SECTION = "libs"
LICENSE = "GPL"

PR = "r0"

SRC_URI = "http://github.com/downloads/stephane/libmodbus/libmodbus-${PV}.tar.gz"

inherit autotools pkgconfig

SRC_URI[md5sum] = "8c54945fdd07c7dd6f3bfcfe61ac0ace"
SRC_URI[sha256sum] = "eeb5df3e7b07a1a7a5a33134cdda0f46a748ce66ccd6e1bf9949c07ef9f91fb1"

