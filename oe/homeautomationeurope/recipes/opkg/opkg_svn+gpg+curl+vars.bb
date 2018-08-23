require ${OERECIPES}/${PN}/opkg.inc

# Original FILESPATH from bitbake.conf:
#FILESPATH = "${FILE_DIRNAME}/${PF}:${FILE_DIRNAME}/${P}:${FILE_DIRNAME}/${PN}:${FILE_DIRNAME}/files:${FILE_DIRNAME}"
FILESPATH_append = ":${OERECIPES}/${PN}/${PF}:${OERECIPES}/${PN}/${P}:${OERECIPES}/${PN}/${PN}:${OERECIPES}/${PN}/files:${OERECIPES}/${PN}"



DEFAULT_PREFERENCE = "99"
COMPATIBLE_MACHINE = "quby2"

# in machine/quby2.conf: SRCREV_pn-opkg = "635"
#SRCREV := "633"
DEPENDS += "gpgme curl"
RDEPENDS += "gpgme curl"

# Originals:
#PR = "${INC_PR}"
#PV = "0.1.8+svnr${SRCPV}"
# Overrides: -gcv for g(pg)c(url)v(ars)
PR = "r7-gcv"
# opkg compare-versions '0.1.8+svns123' '>' '0.1.8+svnrHEAD': TRUE -- so that's why we need the "s"
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

require ${OERECIPES}/${PN}/update-alternatives-merge.inc

EXTRA_OECONF_append_visstrim_m10 = " --with-opkglockfile=/var/lock/opkg.lock"

EXTRA_OECONF += " --enable-gpg \
                  --disable-openssl \
                  --disable-ssl-curl \
                  --enable-curl \
                  --disable-sha256" 
                  
SRC_URI += "\
	file://010-add-tmp-update.status.vars.patch \
	file://020-add-curl-stalled-download-timeout.patch \
	file://030-fix-gpgme-trusted-to-pubring.patch \
"
