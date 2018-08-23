DESCRIPTION = "HAE checkroot init script"
SECTION = "base"
PRIORITY = "required"
DEPENDS = "e2fsprogs"
RDEPENDS_${PN} = "initscripts e2fsprogs-e2fsck e2fsprogs-tune2fs"
LICENSE = "GPL"
PR = "r5"

SRC_URI = "\
	file://checkroot \
	file://S04fix-early-dev \
"

KERNEL_VERSION = ""

do_install () {

	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/rcS.d

	install -m 0755    ${WORKDIR}/S04fix-early-dev	${D}${sysconfdir}/rcS.d
	install -m 0755    ${WORKDIR}/checkroot			${D}${sysconfdir}/init.d

	ln -sf		../init.d/checkroot		${D}${sysconfdir}/rcS.d/S10checkroot
}

pkg_postinst_${PN} () {
	
	[ "$D" ] && SYNC="" || SYNC="sync"
	
	if grep -q '/dev/shm' $D/etc/fstab
	then
		echo '* Removing from /etc/fstab: /dev/shm + /media/ram'
		sed $D/etc/fstab -r \
			-e '/.+\/media\/ram.+/d' \
			-e '/.+\/dev\/shm.+/d' \
			> $D/etc/fstab.new
		$SYNC
		mv $D/etc/fstab.new $D/etc/fstab
	fi
	
	if grep -q 'ENABLE_ROOTFS_FSCK=no' $D/etc/default/rcS
	then
		echo '* Enabling /etc/default/rcS: ENABLE_ROOTFS_FSCK=yes + VERBOSE=no'
		sed $D/etc/default/rcS -r \
			-e 's,ENABLE_ROOTFS_FSCK=no,ENABLE_ROOTFS_FSCK=yes,' \
			-e 's,VERBOSE=yes,VERBOSE=no,' \
			> $D/etc/default/rcS.new
		$SYNC
		mv $D/etc/default/rcS.new $D/etc/default/rcS
	fi

	$SYNC
} 
