DESCRIPTION = "Library for pedagogically-oriented open source site containing software that is broadly useful for image processing and image analysis applications."
HOMEPAGE = "http://www.leptonica.com/"
SECTION = "libs"
LICENSE = "GPL"

PR = "r0"

SRC_URI = "http://www.leptonica.com/source/leptonica-${PV}.tar.gz"

inherit autotools pkgconfig

SRC_URI[md5sum] = "5cd7092f9ff2ca7e3f3e73bfcd556403"
SRC_URI[sha256sum] = "acefb6c50db2913f117afdbb19a549f7409cf8fa5d9a435692cb10ff89a81f8f"

