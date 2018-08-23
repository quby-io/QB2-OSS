###########################################################################
# 
#  Psyco top-level file of the Psyco package.
#   Copyright (C) 2001-2002  Armin Rigo et.al.

"""Psyco -- the Python Specializing Compiler.

Typical usage: add the following lines to your application's main module,
preferably after the other imports:

try:
    import psyco
    psyco.full()
except ImportError:
    print 'Psyco not installed, the program will just run slower'
"""
###########################################################################


#
# This module is present to make 'psyco' a package and to
# publish the main functions and variables.
#
# More documentation can be found in core.py.
#

import sys
# Try to import the dynamic-loading _psyco and report errors
# Sorry, the following does not work under Windows
##import platform 
##if platform.processor() != 'i386': 
##    raise ImportError, "Only i386 is supported." 
try:
    import _psyco
except ImportError, e:
    extramsg = ''
    import sys, imp
    try:
        file, filename, (suffix, mode, type) = imp.find_module('_psyco', __path__)
    except ImportError:
        ext = [suffix for suffix, mode, type in imp.get_suffixes()
               if type == imp.C_EXTENSION]
        if ext:
            extramsg = (" (cannot locate the compiled extension '_psyco%s' "
                        "in the package path '%s')" % (ext[0], '; '.join(__path__)))
    else:
        extramsg = (" (check that the compiled extension '%s' is for "
                    "the correct Python version; this is Python %s)" %
                    (filename, sys.version.split()[0]))
    raise ImportError, str(e) + extramsg

# Publish important data by importing them in the package
from support import __version__, error, warning, _getrealframe, _getemulframe
from support import version_info, __version__ as hexversion
from core import full, stop, cannotcompile
from core import log, bind, unbind, proxy, unproxy, dumpcodebuf
from _psyco import setfilter
from _psyco import compact, compacttype
import classes

# Extract build information
import os as _os, time as _time
where_am_i = _psyco.__file__
when_was_i_born = _time.ctime(_os.stat(where_am_i).st_mtime)
del _os, _time

import psyco.builtin    # effect: registers builtins in psyco
import psyco.clibrary   # effect: registers c function surrogates in psyco
