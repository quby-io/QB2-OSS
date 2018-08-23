inherit image-hae



PR = "r34"



























LICENSE = "MIT"
PROVIDES = "image-flavor-${FLAVOR}-${PACKAGE_ARCH}"


FILESPATH = "${FILE_DIRNAME}/image-${PACKAGE_ARCH}:${FILE_DIRNAME}/files"


export IMAGE_BASENAME = "${PACKAGE_ARCH}-${FLAVOR}-${PR}"
export IMAGE_NAME = "${IMAGE_BASENAME}-${DATETIME}"
export IMAGE_LINK_NAME = "${IMAGE_BASENAME}"

DEPENDS_COOK_RECIPES += "gdb strace bzip2 screen"
DEPENDS_COOK_RECIPES += "samba-nmbd"


RECIPES_AND_DEPENDS_TOOLS = "joe wget less lsof"
RECIPES_AND_DEPENDS_NET = "netcat iptstate chrony"
RECIPES_AND_DEPENDS_LIBS  = "ncurses jpeg"


IMAGE_DEV_MANAGER = ""
IMAGE_INIT_MANAGER = "sysvinit sysvinit-pidof"
IMAGE_INITSCRIPTS = "initscripts"
IMAGE_LOGIN_MANAGER = "tinylogin"
DISTRO_SSH_DAEMON = "dropbear"

RECIPES_AND_DEPENDS = "\
	${DISTRO_SSH_DAEMON} \
	base-files \
	base-passwd \
	busybox \
	openvpn \
	openssl \
	netbase \
	module-init-tools \
	modutils-initscripts \
	update-modules \
	mtd-utils \
	i2c-tools \
	${RECIPES_AND_DEPENDS_TOOLS} \
	${RECIPES_AND_DEPENDS_LIBS} \
	${RECIPES_AND_DEPENDS_NET} \
"

DEPENDS_COOK_RECIPES   += "libpng"
IMAGE_INSTALL_PACKAGES += "libpng12"

DEPENDS_COOK_RECIPES   += '${@base_conditional("${MACHINE_FEATURES}", "alsa", "alsa-lib alsa-utils", "", d)}'

DEPENDS_COOK_RECIPES   += 'tslib'
IMAGE_INSTALL_PACKAGES += 'tslib tslib-calibrate'

DEPENDS_COOK_RECIPES   += "iptables tzdata"
IMAGE_INSTALL_PACKAGES += "iptables iptables-utils tzdata-europe"

DEPENDS_COOK_RECIPES   += "curl"
IMAGE_INSTALL_PACKAGES += "curl"



DEPENDS_COOK_RECIPES += "wireless-tools wpa-supplicant"



SRC_URI = "\
	file://functions.hae \
	file://banner \
	file://inittab \
	file://fstab \
	file://profile.d_defaults.sh \
	file://interfaces \
"


IMAGE_DEVICE_TABLES = ""

IMAGE_LINGUAS = "en-us nl-nl de-de en-gb fr-fr es-es it-it"
IMAGE_INSTALL_PACKAGES += "glibc-gconv-iso8859-1"


FEED_URIS = ""


export TAR_OPTIONS=--wildcards


ROOTFS_POSTPROCESS_COMMAND = 'rootfs_postprocess_image_base_inc;'

rootfs_postprocess_image_base_inc() {

	create_etc_timestamp
	

	mknod ${IMAGE_ROOTFS}/dev/null c 1 3
	mknod ${IMAGE_ROOTFS}/dev/console c 5 1

	
	rm -f ${IMAGE_ROOTFS}/etc/*.d/*openvpn || true		#*/
	rm -f ${IMAGE_ROOTFS}/etc/*.d/nscd || true			#*/
	rm -f ${IMAGE_ROOTFS}/usr/lib/libstdc++.so.*-gdb.py
	
	for p in ${WORKDIR}/*-*.patch						#*/
	do
		if [ -f ${p} ]
		then
			echo "Applying patch ${p}"
			patch -i ${p} -u -p0 -d ${IMAGE_ROOTFS}
		fi
	done
	

	sed --in-place -r -e 's,^root:.*$,root:qcpQ7kiIsIRwM:0:0:root:/root:/bin/sh,'	${IMAGE_ROOTFS}/etc/passwd
	sed --in-place -r -e 's,# UTC=yes,UTC=yes,'										${IMAGE_ROOTFS}/etc/default/rcS
	sed --in-place -r -e '/(wtmp|utmp|lastlog)/d'									${IMAGE_ROOTFS}/etc/default/volatiles/00_core	
	sed --in-place -r -e 's/^.*-nobackups.*$/-nobackups/'							${IMAGE_ROOTFS}/etc/joe/joerc
	sed --in-place -r -e 's,=Tag\t\tblue,=Tag\t\tbold blue,'						${IMAGE_ROOTFS}/etc/joe/syntax/xml.jsf
	echo 'Europe/Amsterdam'														  > ${IMAGE_ROOTFS}/etc/timezone
	fgrep -q /HCBv2/lib ${IMAGE_ROOTFS}/etc/ld.so.conf || echo "/HCBv2/lib"		 >> ${IMAGE_ROOTFS}/etc/ld.so.conf
	sed --in-place -r -e '/swap/d'													${IMAGE_ROOTFS}/etc/init.d/umountfs


	rm -rf ${IMAGE_ROOTFS}/usr/share/gettext
	rm -rf ${IMAGE_ROOTFS}/media
	rm -rf ${IMAGE_ROOTFS}/mnt/?*
	rm -rf ${IMAGE_ROOTFS}/tmp/?*Image*
	find ${IMAGE_ROOTFS}/usr/share/terminfo -type f | egrep -v "dummy|dumb|xterm|screen|linux|vt100|vt200" | xargs rm -f || true
	find ${IMAGE_ROOTFS}/usr/share/terminfo -type d | xargs rmdir --ignore-fail-on-non-empty || true

	cp ${WORKDIR}/banner				${IMAGE_ROOTFS}/etc/init.d/banner
	cp ${WORKDIR}/functions.hae			${IMAGE_ROOTFS}/etc/init.d/functions.hae
	cp ${WORKDIR}/inittab				${IMAGE_ROOTFS}/etc/inittab
	cp ${WORKDIR}/fstab					${IMAGE_ROOTFS}/etc/fstab
	cp ${WORKDIR}/profile.d_defaults.sh	${IMAGE_ROOTFS}/etc/profile.d/defaults.sh
	cp ${WORKDIR}/interfaces			${IMAGE_ROOTFS}/etc/network/interfaces

	mv ${IMAGE_ROOTFS}/home/root 		${IMAGE_ROOTFS}/root
	mkdir -p ${IMAGE_ROOTFS}/etc/modutils/
	rmdir ${IMAGE_ROOTFS}/home || true
	
	install -m 0700 -d							${IMAGE_ROOTFS}/root/.ssh


	cat - > ${IMAGE_ROOTFS}/sbin/unconfigured.sh <<-EOF
		
		
		export PATH="/usr/local/bin:/usr/bin:/bin:/usr/local/sbin:/usr/sbin:/sbin"
	
		MODDIR=/lib/modules/\`uname -r\`
		[ -d \$MODDIR ] || mkdir -p \$MODDIR
		depmod -a
		
		update-modules
		
		rm /sbin/unconfigured.sh
	
	EOF
	chmod a+x ${IMAGE_ROOTFS}/sbin/unconfigured.sh

}



do_rootfs_append() { 

	for f in ${IMAGE_FSTYPES}
	do
		IMGFILE=${IMAGE_NAME}.rootfs.${f}
		
		if [ -f ${DEPLOY_DIR_IMAGE}/${IMGFILE}fs ]
		then
			rm -f ${DEPLOY_DIR_IMAGE}/${IMGFILE}fs
		fi
		
		if [ `stat -c'%s' ${DEPLOY_DIR_IMAGE}/${IMGFILE}` -gt 15000000 ]
		then
			gzip ${DEPLOY_DIR_IMAGE}/${IMGFILE}
			IMGFILE=${IMGFILE}.gz
		fi
		oewarn "Created ${DEPLOY_DIR_IMAGE}/${IMGFILE}:"
		ls -la ${DEPLOY_DIR_IMAGE}/${IMGFILE}
		chmod a+r ${DEPLOY_DIR_IMAGE}/${IMGFILE}
		
		rm -f ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.${f}

	done
	
}

