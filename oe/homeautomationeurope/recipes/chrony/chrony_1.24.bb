DESCRIPTION = "Chrony"
SECTION = "console/network"
PRIORITY = "optional"
DEPENDS = ""
LICENSE = "GPL"


SRC_URI = "http://download.tuxfamily.org/chrony/chrony-${PV}.tar.gz"
SRC_URI += "file://etc-init.d-chrony"
SRC_URI += "file://chrony.conf"


PR = "r0"

inherit autotools


EXTRA_OECONF = "\
	--disable-readline \
"


do_install_append() {
	install -d ${D}/${sysconfdir}/init.d
	install -m 755 ${WORKDIR}/etc-init.d-chrony ${D}/${sysconfdir}/init.d/chrony
	install -m 644 ${WORKDIR}/chrony.conf       ${D}/${sysconfdir}/chrony.conf
}

#inherit update-rc.d
#INITSCRIPT_NAME = "chrony"
#INITSCRIPT_PARAMS = "s60 k20"

pkg_postinst() {
#!/bin/sh
	export PATH="/bin:/usr/bin:/sbin:/usr/sbin:$PATH"
	
	update-rc.d chrony defaults
	
	#todo: these?
	#iptables -I  HCB-INPUT -p tcp -m tcp --dport 123 -j ACCEPT
	#iptables -I  HCB-INPUT -p udp -m udp --dport 123 -j ACCEPT
	#iptables-save > /etc/default/iptables.conf
}


pkg_postrm() {
#!/bin/sh
	export PATH="/bin:/usr/bin:/sbin:/usr/sbin:$PATH"
	
	update-rc.d chrony remove
	
	#todo: remove these?
	#iptables -I  HCB-INPUT -p tcp -m tcp --dport 123 -j ACCEPT
	#iptables -I  HCB-INPUT -p udp -m udp --dport 123 -j ACCEPT
	#iptables-save > /etc/default/iptables.conf
}

SRC_URI[md5sum] = "8849e95428f43c5ab2692a2812653e65"
SRC_URI[sha256sum] = "dd0e6c98a7e094d760d83a1681f85d84f3a5103400c49b4a9e062bbe02ceb215"


