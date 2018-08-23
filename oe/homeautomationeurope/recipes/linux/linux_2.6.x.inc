require ${OEBASE}/openembedded/recipes/linux/linux.inc


PR = "${REV}"

FLAVOR="hae"

KERNEL_RELEASE = "${PV}-${PR}"
DEFCONFIG = defconfig-${PV}-${PR}


FILESPATH = "${FILE_DIRNAME}/linux-${MACHINE}-${FLAVOR}:${FILE_DIRNAME}/linux-${MACHINE}:${FILE_DIRNAME}/files"



KERNEL_IMAGE_BASE_NAME		= "${KERNEL_IMAGETYPE}-${PACKAGE_ARCH}-${FLAVOR}-${KERNEL_RELEASE}-${DATETIME}"
KERNEL_IMAGE_SYMLINK_NAME	= "${KERNEL_IMAGETYPE}-${PACKAGE_ARCH}-${FLAVOR}"




do_configure_prepend() {

	touch ${WORKDIR}/defconfig

}

do_configure() {
	
	[ -e ${WORKDIR}/logo_linux_clut224-${FLAVOR}.ppm ] && \
		install  ${WORKDIR}/logo_linux_clut224-${FLAVOR}.ppm	${S}/drivers/video/logo/logo_linux_clut224.ppm 

	[ -f ${WORKDIR}/${DEFCONFIG} ] && mv ${WORKDIR}/${DEFCONFIG} ${WORKDIR}/defconfig

	install  ${WORKDIR}/defconfig  ${S}/.config
	
	echo "-${PR}" > ${S}/localversion-hae

	oe_runmake oldconfig

}

do_flavorize() {
	
	mkdir -p ${D}/boot/
	
	mv ${KERNEL_OUTPUT} ${KERNEL_OUTPUT}-original
	mv ${S}/drivers/video/logo/logo_linux_clut224.ppm ${S}/drivers/video/logo/logo_linux_clut224.ppm-original
	
	for logo in ${WORKDIR}/logo_linux_clut224-*.ppm
	do
		[ -f $logo ] || continue
		
		flavor="`basename $logo .ppm | sed -r -e 's,logo_linux_clut224-,,'`"
		
		oewarn "Flavorizing '$flavor' with '$logo' ..."
		
		install $logo ${S}/drivers/video/logo/logo_linux_clut224.ppm
		
		kernel_do_compile
		do_sizecheck
		
		mv ${KERNEL_OUTPUT} ${KERNEL_OUTPUT}-${KERNEL_RELEASE}-$flavor
	done

	mv ${KERNEL_OUTPUT}-original ${KERNEL_OUTPUT}
	mv ${S}/drivers/video/logo/logo_linux_clut224.ppm-original ${S}/drivers/video/logo/logo_linux_clut224.ppm

}
addtask flavorize before do_sizecheck after do_compile


do_install_append() {

	
	for dir in usr/src/linux-${PV}-${PR} lib/modules/${PV}-${PR}/build lib/modules/${PV}-${PR}/source
	do
		if [ -e ${D}/$dir ]
		then
			rm -rf ${D}/$dir
		fi
	done
}


do_deploy_append() {
	rm -f ${DEPLOY_DIR_IMAGE}/${MODULES_IMAGE_BASE_NAME}.tgz
	rm -f ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE_SYMLINK_NAME}.bin
}


python do_package_write_flavors () {

	import bb, os, glob, re
	
	pr = bb.data.getVar('PR', d, True)
	pv = bb.data.getVar('PV', d, True)
	kOutputPathToImg = bb.data.getVar('KERNEL_OUTPUT', d, True)
	kRelease = bb.data.getVar('KERNEL_RELEASE', d, True)
	kImgType = bb.data.getVar('KERNEL_IMAGETYPE', d, True)
	wdir = bb.data.getVar('WORKDIR', d, True)
	sdir = bb.data.getVar('S', d, True)
	ddir = os.path.join(wdir, 'packages-split', 'kernel-image')

	orig_packages = bb.data.getVar('PACKAGES', d, True)
	bb.data.setVar('PACKAGES', 'kernel-image', d)
	
	logo_re = re.compile('^.+/logo_linux_clut224-(.+)\.ppm$')
	flavors = []
	for k in glob.glob(os.path.join(wdir, "logo_linux_clut224-*.ppm")):
		m = logo_re.match(k)
		if m:
			flavors.append(m.group(1));

	for flavor in flavors:

		bb.data.setVar('PR_kernel-image', "%s-%s" % (pr, flavor), d)
		
		kimage_src = os.path.join(sdir, "%s-%s-%s" % (kOutputPathToImg, kRelease, flavor))
		kimage_dst = os.path.join(ddir, 'boot', "%s-%s-%s" % (kImgType, kRelease, flavor))
		
		for k in glob.glob(os.path.join(ddir, 'boot', "*Image-*")):
			os.unlink(k)

		bb.copyfile(kimage_src, kimage_dst)
	
		bb.build.exec_func("do_package_ipk", d)

   	bb.data.setVar('PACKAGES', orig_packages, d)
	bb.data.setVar('PR_kernel-image', kRelease, d)
}
addtask package_write_flavors before do_package_write after do_package_write_ipk







FILES_kernel-vmlinux = ""




PKG_kernel-base = "kernel"
PKG_kernel-image = "kernel-image"



PACKAGES = "kernel-image"

 
 
 

PR_kernel-image = "${PR}"



PACKAGES = "kernel kernel-base kernel-image kernel-dev"






NANDWRITE_ARGS ?= '${@base_conditional("MACHINE", "quby1", "-n -a -p", "-a -p", d)}'


pkg_postinst_kernel-image() {
	[ "x$D" != "x" ] && exit 1
	
	MTD="`grep '"kernel"' /proc/mtd | cut -d':' -f1 | tail -n 1`"
	IMG="/boot/${KERNEL_IMAGETYPE}-${KERNEL_RELEASE}"
	
	echo "* About to flash /dev/$MTD with kernel image $IMG ..."

	if [ "$MTD" = "" ]
	then
		echo "* Flashing failed - mtd partition 'kernel' not found!"
		exit 2
	fi

	if [ ! -s $IMG ]
	then
		echo "* Flashing failed - image not found/zero!"
		exit 1
	fi

	MTD_SIZE="`mtd_debug info /dev/$MTD | grep mtd.size | cut -d' ' -f3`"
	IMG_SIZE="`/bin/ls -l "$IMG" | xargs | cut -d' ' -f5`"
	if [ $IMG_SIZE -gt $MTD_SIZE ]
	then
		echo "* Flashing failed - partition ($MTD_SIZE) too small for image ($IMG_SIZE)!"
		exit 3
	fi
	
	IMG_FROM_MTD="/tmp/${KERNEL_IMAGETYPE}-current"
	dd if=/dev/$MTD of=$IMG_FROM_MTD bs=$IMG_SIZE count=1 2>&1 | grep -v -E "(records|seconds)"
	cmp -s $IMG $IMG_FROM_MTD && IDENTICAL=1
	rm -f $IMG_FROM_MTD

	if [ "$IDENTICAL" ]
	then
		echo "* Flashing not needed - $IMG_SIZE data bytes equal."
	else
		echo "* Images differ, writing kernel image to /dev/$MTD ..."
		flash_eraseall /dev/$MTD
		nandwrite ${NANDWRITE_ARGS} /dev/$MTD $IMG
		echo "* Flashing /dev/$MTD with kernel image $IMG done."
	fi
	
	rm -f $IMG
	touch $IMG
	
	exit 0
}
