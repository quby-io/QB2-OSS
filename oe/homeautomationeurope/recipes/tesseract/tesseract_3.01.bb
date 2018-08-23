DESCRIPTION = "A commercial quality OCR engine"
LICENSE = "APL + others"

DEPENDS = "leptonica"
DEPENDS += "zlib"
DEPENDS += "tiff"

RDEPENDS = "leptonica"
RDEPENDS += "tiff"


SRC_URI = "http://tesseract-ocr.googlecode.com/files/tesseract-${PV}.tar.gz"

inherit autotools pkgconfig



#do_configure_prepend() {
#
#	pwd
#		
#	./autogen.sh
#
#}

SRC_URI[md5sum] = "1ba496e51a42358fb9d3ffe781b2d20a"
SRC_URI[sha256sum] = "c24b0bd278291bc93ab242f93841c1d8743689c943bd804afbc5b898dc0a1c9b"

