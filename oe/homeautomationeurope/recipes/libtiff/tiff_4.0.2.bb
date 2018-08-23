DESCRIPTION = "This software provides support for the Tag Image File Format (TIFF)"
LICENSE = "${PN}"
HOMEPAGE = "http://www.remotesensing.org/libtiff/"
DEPENDS = "zlib jpeg lzo"
#PV = "3.9.4+4.0.0beta6"

PR = "r0"

SRC_URI = "http://download.osgeo.org/libtiff/tiff-${PV}.tar.gz "
#SRC_URI = "http://download.osgeo.org/libtiff/tiff-4.0.0beta6.tar.gz "
#	   file://tiff-lp589145.diff;striplevel=0 \
#	   file://tiff-ojpeg-null-stripbytecount.diff;striplevel=0"

SRC_URI[md5sum] = "04a08fa1e07e696e820a0c3f32465a13"
SRC_URI[sha256sum] = "aa29f1f5bfe3f443c3eb4dac472ebde15adc8ff0464b83376f35e3b2fef935da"


#S = "${WORKDIR}/tiff-4.0.0beta6"

inherit autotools

EXTRA_OECONF = "--without-x --disable-rpath"

do_configure_append() {
    # Fix RPATH issues.
    sed -i ${S}/config.status -e s,^\\\(hardcode_into_libs=\\\).*$,\\1\'no\',
    ${S}/config.status
}

PACKAGES =+ "tiffxx tiffxx-dbg tiffxx-dev tiff-utils tiff-utils-dbg"
FILES_tiffxx = "${libdir}/libtiffxx.so.*"
FILES_tiffxx-dev = "${libdir}/libtiffxx.so ${libdir}/libtiffxx.*a"
FILES_tiffxx-dbg += "${libdir}/.debug/libtiffxx.so*"
FILES_tiff-utils = "${bindir}/*"
FILES_tiff-utils-dbg += "${bindir}/.debug/"

BBCLASSEXTEND = "native"
