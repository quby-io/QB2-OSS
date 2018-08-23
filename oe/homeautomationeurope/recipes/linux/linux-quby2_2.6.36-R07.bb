require linux-quby2_2.6.x.inc


REV = "h11"

PD_REL_BSP  = "08"

PD_REL_KERN = "07"
PD_REL_WLAN = "02"
PD_REL_ZWAV = "01"

#PSC6500110141R02.tgz      : PSC ED2.0 Display Linux Kernel
#PSC6500110152R01.tgz      : PSC ED2.0 Display WLAN driver
#PSC6500110154R01.tgz      : PSC ED2.0 Display Z-Wave300 driver

# generate the names of the relevant archives + dirs
PD_BSP_DIR = "."

PD_KSRC_KERN = "PSC6500110141R${PD_REL_KERN}.tgz"
PD_KSRC_WLAN = "PSC6500110152R${PD_REL_WLAN}.tgz"
PD_KSRC_ZWAV = "PSC6500110154R${PD_REL_ZWAV}.tgz"

S = "${WORKDIR}/linux_r${PD_REL_KERN}"
PD_KDIR_WLAN = "rtl8191su_wlan_driver_r${PD_REL_WLAN}"
PD_KDIR_ZWAV = "zwave300_driver_r${PD_REL_ZWAV}"

#	file://010-mx2fb-no-show-logo-v2.patch \
#	file://031-bl-max-100.patch \
#	file://050-mach-ed20-add-watchdog.patch \
#	file://017-mxcfb-modedb-30MHz.patch \
SRC_URI += "\
	file://${PD_BSP_DIR}/${PD_KSRC_KERN} \
	file://015-mx2fb-preserve-fb.patch \
	file://040-backlight-ramp.patch \
	file://050-imx2wdt-add-bootstatus.patch \
	file://${PD_BSP_DIR}/${PD_KSRC_WLAN} \
	file://${PD_BSP_DIR}/${PD_KSRC_ZWAV} \
"

do_slipstream_modsrc() {

	# remove inconsistent Prodrive release number from scripts/setlocalversion
	sed -i "${S}/scripts/setlocalversion" -r -e '/^printf/d'
	# re-add in proper location: 'localversion-0prodrive' is before 'localversion-hae'
	echo "-R${PD_REL_KERN}" > "${S}/localversion-0prodrive"

	mkdir -p "${S}/drivers/prodrive/"
	
	# move unpacked module sources
	mv -v "${WORKDIR}/${PD_KDIR_WLAN}" "${S}/drivers/prodrive/${PD_KDIR_WLAN}"
	mv -v "${WORKDIR}/${PD_KDIR_ZWAV}" "${S}/drivers/prodrive/${PD_KDIR_ZWAV}"
	
	# add them to the list of items to process
	echo ""                                   >> "${S}/drivers/Makefile"
	echo "# Prodrive external modules"        >> "${S}/drivers/Makefile"
	echo "obj-y	+= prodrive/${PD_KDIR_WLAN}/" >> "${S}/drivers/Makefile"
	echo "obj-y	+= prodrive/${PD_KDIR_ZWAV}/" >> "${S}/drivers/Makefile"
	
	# fix broken files
	sed -r -i "${S}/drivers/prodrive/${PD_KDIR_WLAN}/Makefile" \
		-e 's,export TOPDIR,#export TOPDIR,' \
		-e 's,..TOPDIR./,$(src)/,' \
		-e 's,^.\(shell cp .+$,EXTRA_CFLAGS += -DCONFIG_LITTLE_ENDIAN,' \
		-e 's,^CONFIG_BUILT_IN.+$,CONFIG_BUILT_IN = y,'

	# CONFIG_BUILT_IN = y: make as builtin!

	sed -r -i "${S}/drivers/prodrive/${PD_KDIR_WLAN}/config" \
		-e 's,CONFIG_PLATFORM_ARM9.+$,CONFIG_PLATFORM_ARM9		=	n,'

	
}
addtask slipstream_modsrc before do_patch after do_unpack


# these are now built-in:
RPROVIDES_kernel-image += "kernel-module-8712u"
#RPROVIDES_kernel-image += "kernel-module-ac97-bus"
RPROVIDES_kernel-image += "kernel-module-cfg80211"
RPROVIDES_kernel-image += "kernel-module-cp210x"
RPROVIDES_kernel-image += "kernel-module-ftdi-sio"
RPROVIDES_kernel-image += "kernel-module-ip-tables"
RPROVIDES_kernel-image += "kernel-module-ip6-tables"
RPROVIDES_kernel-image += "kernel-module-ip6table-filter"
RPROVIDES_kernel-image += "kernel-module-ipt-addrtype"
RPROVIDES_kernel-image += "kernel-module-ipt-log"
RPROVIDES_kernel-image += "kernel-module-ipt-masquerade"
RPROVIDES_kernel-image += "kernel-module-ipt-redirect"
RPROVIDES_kernel-image += "kernel-module-ipt-reject"
RPROVIDES_kernel-image += "kernel-module-iptable-filter"
RPROVIDES_kernel-image += "kernel-module-iptable-mangle"
RPROVIDES_kernel-image += "kernel-module-iptable-nat"
RPROVIDES_kernel-image += "kernel-module-ipv6"
RPROVIDES_kernel-image += "kernel-module-lib80211"
RPROVIDES_kernel-image += "kernel-module-mac80211"
RPROVIDES_kernel-image += "kernel-module-nf-conntrack-ipv4"
RPROVIDES_kernel-image += "kernel-module-nf-conntrack-ipv6"
RPROVIDES_kernel-image += "kernel-module-nf-conntrack-netbios-ns"
RPROVIDES_kernel-image += "kernel-module-nf-conntrack"
RPROVIDES_kernel-image += "kernel-module-nf-defrag-ipv4"
RPROVIDES_kernel-image += "kernel-module-nf-nat"
RPROVIDES_kernel-image += "kernel-module-pl2303"
#RPROVIDES_kernel-image += "kernel-module-rtl8187"
#RPROVIDES_kernel-image += "kernel-module-sit"
#RPROVIDES_kernel-image += "kernel-module-snd-hrtimer"
#RPROVIDES_kernel-image += "kernel-module-snd-mixer-oss"
#RPROVIDES_kernel-image += "kernel-module-snd-page-alloc"
#RPROVIDES_kernel-image += "kernel-module-snd-pcm-oss"
#RPROVIDES_kernel-image += "kernel-module-snd-pcm"
#RPROVIDES_kernel-image += "kernel-module-snd-soc-core"
#RPROVIDES_kernel-image += "kernel-module-snd-soc-ecr2-aic3100"
#RPROVIDES_kernel-image += "kernel-module-snd-soc-imx"
#RPROVIDES_kernel-image += "kernel-module-snd-soc-tlv320aic3111"
#RPROVIDES_kernel-image += "kernel-module-snd-timer"
RPROVIDES_kernel-image += "kernel-module-ts-bm"
RPROVIDES_kernel-image += "kernel-module-ts-fsm"
RPROVIDES_kernel-image += "kernel-module-ts-kmp"
RPROVIDES_kernel-image += "kernel-module-tunnel4"
RPROVIDES_kernel-image += "kernel-module-xfrm6-mode-beet"
RPROVIDES_kernel-image += "kernel-module-xfrm6-mode-transport"
RPROVIDES_kernel-image += "kernel-module-xfrm6-mode-tunnel"
RPROVIDES_kernel-image += "kernel-module-xt-iprange"
RPROVIDES_kernel-image += "kernel-module-xt-length"
RPROVIDES_kernel-image += "kernel-module-xt-limit"
RPROVIDES_kernel-image += "kernel-module-xt-mark"
RPROVIDES_kernel-image += "kernel-module-xt-multiport"
RPROVIDES_kernel-image += "kernel-module-xt-state"
RPROVIDES_kernel-image += "kernel-module-xt-string"

