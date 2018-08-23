#
# HAE stuff.
#

# override this from "initscripts-hae" to "initscripts"
PV_ = "1.0"
#PN = "initscripts"
#PROVIDES = "initscripts-hae"

include ${OEBASE}/openembedded/recipes/initscripts/initscripts_${PV_}.bb

PP = "initscripts"

#RCONFLICTS_${PN} = "initscripts"

# All other standard definitions inherited from initscripts
# Except the PR which is hacked here.  The format used is
# a suffix
PR := "${PR}.hae52"

#FILESPATHPKG =. "${P}:initscripts-${PV_}:"

FILESPATH_append = ":${OERECIPES}/${PP}/${PP}-${PV_}:${OERECIPES}/${PP}/files/arm:${OERECIPES}/${PP}/files"   

# Prevent:  
#* resolve_conffiles: Existing conffile /etc/device_table is different from the conffile in the new package. The new conffile will be placed at /etc/device_table-opkg.
# caused by:
#CONFFILES_${PN} += "${sysconfdir}/device_table"
CONFFILES_${PN} := ""

#PACKAGES = "${PN}"

#SRC_URI += "file://openprotium/devfs.sh"

# Without this it is not possible to patch checkroot.sh
S = "${WORKDIR}"

do_install_append() {

	update-rc.d -r ${D} -f banner		remove
	update-rc.d -r ${D}    banner		start  7 S .

	# RTC handling is done by hae-timesync.sh
	rm -f ${D}/etc/default/hwclock
	rm -f ${D}/etc/init.d/hwclock.sh
	rm -f ${D}/etc/init.d/save-rtc.sh
	update-rc.d -r ${D} save-rtc.sh		remove

	# we don't need checkroot.sh, it only messes up fs remounting	
	rm -f ${D}/etc/*.d/*checkroot*

}
