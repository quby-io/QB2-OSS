require linux_2.6.x.inc

DEFAULT_PREFERENCE = "99"
COMPATIBLE_MACHINE = "quby2"

SRC_URI = "\
	file://${DEFCONFIG} \
#	file://logo_linux_clut224-hae.ppm \
#	file://logo_linux_clut224-ene.ppm \
"

# 0x00180000 == 1572864 bytes
# 0x00200000 == 2097152
# 0x00300000 == 3145728
KERNEL_IMAGE_MAXSIZE = "3145728"

# U-Boot pleaze :)
KERNEL_IMAGETYPE = "uImage"
UBOOT_ENTRYPOINT = "0xa0008000"

# these are no longer modules: depended upon by iptables{-utils,}
RPROVIDES_kernel-image += "kernel-module-tun" 
#RPROVIDES_kernel-image += "kernel-module-iptable-filter kernel-module-ipt-log kernel-module-ipt-reject"
