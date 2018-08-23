PR = "r0"

TOOLCHAIN_TARGET_TASK = "task-hae-toolchain-target"
TOOLCHAIN_HOST_TASK = "task-hae-toolchain-host"

require meta-toolchain.bb

#TOOLCHAIN_OUTPUTNAME ?= "${DISTRO}-${SDK_SYS}-${FEED_ARCH}-${TARGET_OS}-${SDK_SUFFIX}" 

SDK_SUFFIX = "toolchain"


