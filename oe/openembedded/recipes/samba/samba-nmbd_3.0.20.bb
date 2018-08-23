require samba-essential.inc
inherit update-rc.d

PR = "r8"

SRC_URI += "file://config-lfs.patch \
       file://quota.patch;striplevel=0 \
       file://init-nmbd \
"




EXTRA_OECONF += '--disable-fam --disable-dnssd --disable-avahi' 

# The file system settings --foodir=dirfoo and overridden unconditionally
# in the samba config by --with-foodir=dirfoo - even if the --with is not
# specified!  Fix that here.  Set the privatedir to /etc/samba/private.
EXTRA_OECONF += "\
	--with-configdir=${sysconfdir}/samba \
	--with-privatedir=${sysconfdir}/samba/private \
	--with-lockdir=${localstatedir}/lock \
	--with-piddir=${localstatedir}/run \
	--with-logfilebase=${localstatedir}/log \
	--with-libdir=${libdir} \
	--with-mandir=${mandir} \
	"

do_install_append() {

	install -d "${D}${sysconfdir}/samba"
	install -d "${D}${sysconfdir}/samba/private"

	install -d "${D}${sysconfdir}/init.d"
	install -c -m 755 ${WORKDIR}/init-nmbd ${D}${sysconfdir}/init.d/samba

	# needed for startup?
	touch ${D}${sysconfdir}/printcap
	
	# we want smb.conf to be explicitly installed when needed
	rm -f ${D}${sysconfdir}/samba/smb.conf 
}

do_configure_prepend() {

        ./script/mkversion.sh
        if [ ! -e acinclude.m4 ]; then
                cat aclocal.m4 > acinclude.m4
        fi

	# the original acinclude.m4 script is busted :(
	sed ${S}/acinclude.m4 --in-place -r \
		-e 's/AC_TRY_RUN\(\[\$1\],\[\$5\],\[\$6\],\[\$7\]\);/AC_TRY_RUN([\$1],[\$5],[\$6],[\$7])/'

}

INITSCRIPT_NAME = "samba"
INITSCRIPT_PARAMS = "defaults 90 20"

PACKAGES = "samba-nmbd"
CONFFILES_${PN} = ""

FILES_${PN} = "\
	       ${libdir}/charset \
	       ${libdir}/*.dat \
	       ${bindir}/nmblookup \
	       ${sbindir}/nmbd \
	       ${sysconfdir} \
	       "

SRC_URI[md5sum] = "68e72ab16334c329901816febc43217a"
SRC_URI[sha256sum] = "e5ecf89ed1be4c2a2f198e9c8b3d60223f2f1172b9d53d5388c08b5377e18e3a"

