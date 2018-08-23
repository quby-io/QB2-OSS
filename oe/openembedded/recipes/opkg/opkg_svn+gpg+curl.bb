require opkg.inc

DEFAULT_PREFERENCE = "99"
COMPATIBLE_MACHINE = "quby2"

#SRCREV := "633"
DEPENDS += "gpgme curl"
RDEPENDS += "gpgme curl"

# Originals:
#PR = "${INC_PR}"
#PV = "0.1.8+svnr${SRCPV}"
# Overrides: -gc for g(pg)(c)url
PR = "r6-gc"
# opkg compare-versions '0.1.8+svns123' '>' '0.1.8+svnrHEAD': TRUE
PV = "0.1.8+svns${SRCREV}"

PROVIDES =+ "virtual/update-alternatives"
RPROVIDES_${PN} = "update-alternatives"
PACKAGES =+ "libopkg-dev libopkg"
RREPLACES_${PN} = "opkg opkg-nogpg opkg-nogpg-nocurl"

FILES_libopkg-dev = "${libdir}/*.a ${libdir}/*.la ${libdir}/*.so"
FILES_libopkg = "${libdir}/*.so.*"
FILES_${PN} += "${datadir}/opkg/intercept"

pkg_postinst_${PN} () {
  update-alternatives --install ${bindir}/opkg opkg ${bindir}/opkg-cl 100
}

pkg_postrm_${PN} () {
  update-alternatives --remove opkg ${bindir}/opkg-cl
}

require update-alternatives-merge.inc

EXTRA_OECONF_append_visstrim_m10 = " --with-opkglockfile=/var/lock/opkg.lock"

EXTRA_OECONF += " --enable-gpg \
                  --disable-openssl \
                  --disable-ssl-curl \
                  --enable-curl \
                  --disable-sha256" 