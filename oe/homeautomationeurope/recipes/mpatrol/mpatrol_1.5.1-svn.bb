DESCRIPTION = "Library for checking memory alloc stuff"
SECTION = "libs"
LICENSE = "GPL"

PV = "1.5.1+svnr${SRCPV}"
PR = "r0"
SRCREV = "2361"

SRC_URI = "svn://mpatrol.svn.sourceforge.net/svnroot/mpatrol;module=trunk;proto=https"
 
S="${WORKDIR}/trunk/pkg/auto"

PR = "r0"

do_configure_prepend () {

	cd ${S}
	chmod a+x *
	./setup
	chmod a+x configure config.sub install-sh ltconfig ltmain.sh missing mkinstalldirs
	
}


inherit autotools pkgconfig

#SRC_URI[md5sum] = "8c54945fdd07c7dd6f3bfcfe61ac0ace"
#SRC_URI[sha256sum] = "eeb5df3e7b07a1a7a5a33134cdda0f46a748ce66ccd6e1bf9949c07ef9f91fb1"

