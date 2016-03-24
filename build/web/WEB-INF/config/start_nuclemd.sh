#!/bin/bash

echo;echo "[ Cluster Settings ]"
echo "=============================================================================================="
echo "Running host ..." `hostname -f`
echo "IP address ....." `/sbin/ifconfig | grep "inet addr:" | head -1 | awk '{print $2}' | awk -F':' '{print $2}'`
echo "Kernel ........." `uname -r`
echo "Distribution ..." `head -n1 /etc/issue`
echo "Arch ..........." `uname -a | awk '{print $12}'`
echo "CPU  ..........." `cat /proc/cpuinfo | grep -i "model name" | head -1 | awk -F ':' '{print $2}'`
echo "Memory ........." `cat /proc/meminfo | grep MemTotal | awk {'print $2'}` KB
echo "Partitions ....." `cat /proc/partitions`
echo "Uptime host ...." `uptime | sed 's/.*up ([^,]*), .*/1/'`
echo "Timestamp ......" `date`
echo "=============================================================================================="

export VO_NAME=$(voms-proxy-info -vo)
export VO_VARNAME=$(echo ${VO_NAME} | sed s/"\."/"_"/g | sed s/"-"/"_"/g | awk '{ print toupper($1) }')
export VO_SWPATH_NAME="VO_"$VO_VARNAME"_SW_DIR"
export VO_SWPATH_CONTENT=$(echo $VO_SWPATH_NAME | awk '{ cmd=sprintf("echo $%s",$1); system(cmd); }')
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib:/usr/lib64

NUCLEMD_LOG="nuclemd.log"
# List of arguments
USERNAME=$1
DEMO=$5
RELEASE=$6

if [ "X${DEMO}" == "Xon" ] ; then
	echo;echo "Running the simulation using the demo files"
        INP_FILE=`basename ${2}`
        CONF_FILE1=`basename ${3}`
        CONF_FILE2=`basename ${4}`
	
	INP_FILE=`echo ${INP_FILE} | awk -F'/' '{print $NF}'`
	CONF_FILE1=`echo ${CONF_FILE1} | awk -F'/' '{print $NF}'`
	CONF_FILE2=`echo ${CONF_FILE2} | awk -F'/' '{print $NF}'`	
        if [ "X${RELEASE}" == "Xnuclemd_ver1" ] ; then
                echo ${INP_FILE} > ./inputfilename.dat
        else
                echo ${INP_FILE} > ./inputc1c2.dat
        fi	

	cp `basename $2` ${INP_FILE} 2>/dev/null
        cp `basename $3` ${CONF_FILE1} 2>/dev/null
        cp `basename $4` ${CONF_FILE2} 2>/dev/null
else
	echo;echo "Running the simulation using input files"
        INP_FILE=`basename ${2}`
        CONF_FILE1=`basename ${3}`
        CONF_FILE2=`basename ${4}`

	INP_FILE=`echo ${INP_FILE} | awk -F'_stripped' '{print $1}' | awk -F"_${USERNAME}_" '{print $2}'`
	CONF_FILE1=`echo ${CONF_FILE1} | awk -F'_stripped' '{print $1}' | awk -F"_${USERNAME}_" '{print $2}'`
	CONF_FILE2=`echo ${CONF_FILE2} | awk -F'_stripped' '{print $1}' | awk -F"_${USERNAME}_" '{print $2}'`
	if [ "X${RELEASE}" == "Xnuclemd_ver1" ] ; then
                echo ${INP_FILE} > ./inputfilename.dat
        else
                echo ${INP_FILE} > ./inputc1c2.dat
        fi
	
	cp `basename $2` ${INP_FILE} 
	cp `basename $3` ${CONF_FILE1}
	cp `basename $4` ${CONF_FILE2}	
fi

echo;echo "[ Display NUCLEMD Settings ]"
echo "-----------------------------------------"
echo "VO_NAME ............: "${VO_NAME}
echo "VO_VARNAME .........: "${VO_VARNAME}
echo "VO_SWPATH_NAME .....: "${VO_SWPATH_NAME}
echo "VO_SWPATH_CONTENT ..: "${VO_SWPATH_CONTENT}
echo "LD_LIBRARY_PATH ....: "${LD_LIBRARY_PATH}
echo "-----------------------------------------"
echo "[ CONF. FILES ] ....: ${INP_FILE}; ${CONF_FILE1}; ${CONF_FILE2}"
if [ "X${RELEASE}" == "Xnuclemd_ver1" ] ; then
        echo "INPUTFILE ..........: inputfilename.dat"
        echo "....................: "`cat inputfilename.dat`
else
        echo "INPUTFILE ..........: inputc1c2.dat"
        echo "....................: "`cat inputc1c2.dat`
fi
echo "USERNAME ...........: "${USERNAME}
echo "DEMO ...............: "${DEMO}
echo "RELEASE ............: "${RELEASE}
echo "-----------------------------------------"

echo;echo "[ Checking input files ]"
if [ ! -e ${INP_FILE} ] && [ ! -e ${CONF_FILE1} ] && [ ! -e ${CONF_FILE2} ] ; then
echo "[ Input files missing! Aborting ]"
exit
else
ls -1 ${PWD}
fi

if [ "X${RELEASE}" == "Xnuclemd_ver1" ] ; then
	WELCOME=" [ STARTING ] the NUCLEMED model v0.1 "
else
	WELCOME=" [ STARTING ] the NUCLEMED model v0.2 "
fi

echo;echo "${WELCOME}"
echo
echo " ███╗   ██╗██╗   ██╗ ██████╗██╗     ███████╗███╗   ███╗██████╗ "
echo " ████╗  ██║██║   ██║██╔════╝██║     ██╔════╝████╗ ████║██╔══██╗"
echo " ██╔██╗ ██║██║   ██║██║     ██║     █████╗  ██╔████╔██║██║  ██║"
echo " ██║╚██╗██║██║   ██║██║     ██║     ██╔══╝  ██║╚██╔╝██║██║  ██║"
echo " ██║ ╚████║╚██████╔╝╚██████╗███████╗███████╗██║ ╚═╝ ██║██████╔╝"
echo " ╚═╝  ╚═══╝ ╚═════╝  ╚═════╝╚══════╝╚══════╝╚═╝     ╚═╝╚═════╝ "

if [ "X${RELEASE}" == "Xnuclemd_ver1" ] ; then
	chmod +x ./nuclemd_v1
	echo "./nuclemd_v1 ${INP_FILE} ${CONF_FILE1} ${CONF_FILE2} 2>${NUCLEMD_LOG} >${NUCLEMD_LOG}"
	./nuclemd_v1 ${INP_FILE} ${CONF_FILE1} ${CONF_FILE2} 2>${NUCLEMD_LOG} >${NUCLEMD_LOG}
else
	chmod +x ./nuclemd_v2
	echo "./nuclemd_v2 ${INP_FILE} ${CONF_FILE1} ${CONF_FILE2} 2>${NUCLEMD_LOG} >${NUCLEMD_LOG}"
	./nuclemd_v2 ${INP_FILE} ${CONF_FILE1} ${CONF_FILE2} 2>${NUCLEMD_LOG} >${NUCLEMD_LOG}
fi

if [ $? -eq 0 ] ; then
echo;echo "NUCLEMD execution [ DONE ]"
ls -1 ${PWD}
else
echo "NUCLEMD execution [ FAILED ]"
echo "Please, check log files. Some errors occurred during the execution of the model."
fi

cat <<EOF >> output.README
#
# README - NUCLEMD
#
# Gianluca GIULIANI, INFN Catania
# <mailto:gianluca.giuliani1@gmail.com>
#
# Giuseppe LA ROCCA, INFN Catania
# <mailto:giuseppe.larocca@ct.infn.it>
#

NUCLEMD is a computer code based on the Constrained Molecular Dynamics model.
The peculiarity of the algorithm consists on the isospin dependence of the nucleon-nucleon cross section and on the
presence of the Majorana Exchange Operator in the nucleon-nucleon collision term.
The code will be devoted to the study of Single and Double Charge Exchange processes in nuclear reactions at low and
intermediate energies.

The aim is to provide theoretical support to the experimental results of the DREAMS collaboration obtained by means of
the MAGNEX spectrometer.

In case of correct execution, the application will produce the following list of files:

~ .std.txt/.err ........................... the standard output/error files;			
~ nuclemd.log ............................. the NUCLEMD output file;				
~ results.tar.gz .......................... the archive containing the NUCLEMD output results.	

EOF

echo;echo "[ CREATING archive with results ]"

if [ "X${RELEASE}" == "Xnuclemd_ver1" ] ; then
	tar cfz results.tar.gz \
        	*_out >/dev/null 2>/dev/null
else
	tar cfz results.tar.gz \
        	gdr*.dat NST*.dat >/dev/null 2>/dev/null
fi

if [ $? -eq 0 ] ; then
echo "CREATING archive with results [ DONE ]"
ls -1 results.tar.gz
else
echo "CREATING archive with results [ FAILED ]"
echo "Please, check log files. Some errors occurred during the creation of the archive."
fi
