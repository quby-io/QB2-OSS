require image-base.inc


COMPATIBLE_MACHINE = "quby2" 
DESCRIPTION = "Image package for Quby2 / base"
FLAVOR = "base"
PROVIDES = "image-${PACKAGE_ARCH}-${FLAVOR}"



DEPENDS_COOK_RECIPES += "linux-${COMPATIBLE_MACHINE}"
RECIPES_AND_DEPENDS += "directfb"


# jffs2, tar(.gz|bz2), cpio(.gz), cramfs, ext2(.gz), ext3(.gz), ext4(.gz|.bz2), # squashfs, squashfs-lzma
IMAGE_FSTYPES = "ubi"





# packages to be bitbake-built:
DEPENDS       += " ${RECIPES_AND_DEPENDS}"
DEPENDS       += " ${DEPENDS_COOK_RECIPES}"

# packages to be ipkg-install'ed:
IMAGE_INSTALL  = "${RECIPES_AND_DEPENDS}"
IMAGE_INSTALL += " ${IMAGE_INSTALL_PACKAGES}"




ROOTFS_POSTPROCESS_COMMAND += 'rootfs_postprocess_image_base_qb2;'

rootfs_postprocess_image_base_qb2() {

	:
	
	# TMP show more during boot
	#sed --in-place -r -e 's,VERBOSE=no,VERBOSE=yes,'	${IMAGE_ROOTFS}/etc/default/rcS
	
	# enter pre-calibration values (HAE)
	# Prototype0: echo "-13696 106 53900864 102 8445 -1728420 65536" > ${IMAGE_ROOTFS}/etc/pointercal
	echo "13399 -2 -1084648 12 -8590 32873042 65536" > ${IMAGE_ROOTFS}/etc/pointercal

	# make sure some modules are loaded (also done in base-t27-* packages, for 'old' rootfs images!)
#	mkdir ${IMAGE_ROOTFS}/etc/modutils || true
#	echo "ehci-hcd"		>  ${IMAGE_ROOTFS}/etc/modutils/qb2-image-base

}
