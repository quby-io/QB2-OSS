require linux-quby2_2.6.x.inc


REV = "h5"

PD_REL_BSP  = "03"

PD_REL_KERN = "04"
PD_REL_WLAN = "01"
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

SRC_URI += "\
	file://${PD_BSP_DIR}/${PD_KSRC_KERN} \
	file://010-mx2fb-no-show-logo-v2.patch \
	file://031-bl-max-100.patch \
	file://040-backlight-ramp.patch \
	file://${PD_BSP_DIR}/${PD_KSRC_WLAN} \
	file://${PD_BSP_DIR}/${PD_KSRC_ZWAV} \
"


do_slipstream_modsrc() {

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
		-e 's,^.\(shell cp .+$,EXTRA_CFLAGS += -DCONFIG_LITTLE_ENDIAN,'

	sed -r -i "${S}/drivers/prodrive/${PD_KDIR_WLAN}/config" \
		-e 's,CONFIG_PLATFORM_ARM9.+$,CONFIG_PLATFORM_ARM9		=	n,'
	
}
addtask slipstream_modsrc before do_patch after do_unpack

