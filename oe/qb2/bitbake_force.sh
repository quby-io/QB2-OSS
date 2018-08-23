
ARCH="`pwd | xargs basename`"
OEBASE="`pwd | xargs dirname`"

TARGET=$1
CMD=$2
OUTDIR="${ARCH}-hae-linux-gnueabi"

[ "$CMD" ] || { echo "Usage: $0 linux-ecr2 configure"; exit 2; }

echo ""
#echo "> touch tmp/stamps/${TARGET}-*.do_${CMD}"
#touch tmp/stamps/${TARGET}-*.do_${CMD}

STAMP="`/bin/ls -t -1 tmp/stamps/$OUTDIR/${TARGET}-*.do_${CMD} | head -n 1`"
echo ""
echo "> rm ${STAMP}"

[ ! -z "${STAMP}" ] && rm -f ${STAMP}

echo ""
echo "> bitbake ${TARGET} $3 $4 $5 $6 $7 $8"
echo ""

bitbake ${TARGET} $3 $4 $5 $6 $7 $8

echo ""
echo "> Done."
echo ""

