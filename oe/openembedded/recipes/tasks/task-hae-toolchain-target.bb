DESCRIPTION = "Target packages for a standalone HAE SDK"
ALLOW_EMPTY = "1"
PR = "r1"
EXCLUDE_FROM_WORLD = "1"

PACKAGES = "${PN}"

# Stuff contained in this SDK is largely taken from task-sdk-base.bb.
# This is a starting point, and nothing more at present -- please fill
# this out with a reasonable set of development tools for a Arago image.
# Also feel free to remove stuff that's silly.

RDEPENDS_${PN} = "\
	task-sdk-bare \
	\
    alsa-dev \
    alsa-lib-dev \
    alsa-utils-dev \
    curl-dev \
    fontconfig-dev \
    freetype-dev \
    jpeg-dev \
    libpng-dev \
    libts-dev \
    directfb-dev \
	\
    lzo-dev \
    libopkg-dev \
    opkg-dev \
    readline-dev \
    zlib-dev \
    ncurses-dev \
    sysvinit-dev \
    \
    libssl \
    libcrypto \
    openssl-dev \
"
#    libusb-compat-dev \
#    libusb1-dev \
#    e2fsprogs-dev \
#    i2c-tools-dev \
#    mtd-utils-dev \
#    libvolume-id-dev \
