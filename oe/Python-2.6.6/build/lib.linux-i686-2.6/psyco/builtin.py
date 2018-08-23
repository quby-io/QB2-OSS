###################################################################################################
# Register pure python function substitutes
# (or as John calls it, Bootstrap PyPy)
from psyco import proxy

#import os
#logpath = os.path.join(os.getcwd(), 'psyco-builtin-log.txt')
#logfile = open(logpath, 'w')

from _psyco import register_builtin2purepython
def register(f):
    #print >> logfile, 'Registering', f.__name__
    original = __builtins__[f.__name__]
    register_builtin2purepython(original, f)

@register
def sum(s, start=0):
    if start != 0 and isinstance(start, basestring):
        raise TypeError("sum() can't sum strings [use ''.join(seq) instead]")
    accm = start
    for elem in s:
        accm += elem
    return accm

@proxy
def _min(*args):
    if not args:
        raise TypeError('min expected 1 arguments, got 0')     
    #2.5 iterable = args[0] if len(args) == 1 else args
    if len(args) == 1:
        iterable = args[0]
    else:
        iterable = args
    if not iterable:
        raise ValueError('min() arg is an empty sequence')
    min_elem = i = 0
    for elem in iterable:
        if elem < min_elem or i==0:
            min_elem = elem
        i += 1
    return min_elem

@proxy
def _min_kw(kwds, *args):
    if len(kwds) > 1:
        raise TypeError('unexpected keyword argument')
    try:
        keyfunc = kwds['key']
    except KeyError:
        raise TypeError('unexpected keyword argument')    
    if not args:
        raise TypeError('min expected 1 arguments, got 0')     
    #2.5 iterable = args[0] if len(args) == 1 else args
    if len(args) == 1:
        iterable = args[0]
    else:
        iterable = args
    if not iterable:
        raise ValueError('min() arg is an empty sequence')
    min_elem = min_value = i = 0
    for elem in iterable:
        elemvalue = keyfunc(elem)
        if elemvalue < min_value or i==0:
            min_value = elemvalue
            min_elem = elem
        i += 1
    return min_elem

@register
def min(*args, **kwds):
    #2.5 return _min_kw(kwds, *args) if kwds else _min(*args)
    if kwds:
        return _min_kw(kwds, *args)
    else:
        return _min(*args)

@proxy
def _max(*args):
    if not args:
        raise TypeError('max expected 1 arguments, got 0')     
    #2.5 iterable = args[0] if len(args) == 1 else args
    if len(args) == 1:
        iterable = args[0]
    else:
        iterable = args
    if not iterable:
        raise ValueError('max() arg is an empty sequence')
    max_elem = i = 0
    for elem in iterable:
        if elem > max_elem or i==0:
            max_elem = elem
        i += 1
    return max_elem

@proxy
def _max_kw(kwds, *args):
    if len(kwds) > 1:
        raise TypeError('unexpected keyword argument')
    try:
        keyfunc = kwds['key']
    except KeyError:
        raise TypeError('unexpected keyword argument')    
    if not args:
        raise TypeError('max expected 1 arguments, got 0')     
    #2.5 iterable = args[0] if len(args) == 1 else args
    if len(args) == 1:
        iterable = args[0]
    else:
        iterable = args
    if not iterable:
        raise ValueError('max() arg is an empty sequence')
    max_elem = max_value = i = 0
    for elem in iterable:
        elemvalue = keyfunc(elem)
        if elemvalue > max_value or i==0:
            max_value = elemvalue
            max_elem = elem
        i += 1
    return max_elem

@register
def max(*args, **kwds):
    #2.5 return _max_kw(kwds, *args) if kwds else _max(*args)
    if kwds:
        return _max_kw(kwds, *args)
    else:
        return _max(*args)

_none = object()

@register
def reduce(function, iterable, initializer=_none):
    accm = initializer
    if accm is _none:
        i = 0
        for elem in iterable:
            if i == 0:
                accm = elem
            else:
                accm = function(accm, elem)
            i = 1
        if i == 0:
            raise TypeError('reduce() of empty sequence with no initial value')
    else:
        for elem in iterable:
            accm = function(accm, elem)
    return accm

##from __builtin__ import filter as _filter
##
##@register
##def filter(func, sequence):
##    if func is None or isinstance(sequence, (basestring, tuple)):
##        return _filter(func, sequence)
##    return [x for x in sequence if func(x)]

try:
    any, all
    @register
    def all(v):
        for item in iter(v):
            if not item:
                return False
        return True

    @register
    def any(v):
        for item in iter(v):
            if item:
                return True
        return False
except NameError:
    pass

class _xrange:
    # XXX register this once generators get attached to the profiler
    # in the meantime, use a basis for coding a CPsyco version of range/xrange.

    def __init__(self, a1, a2=None, a3=None):
        low, high, step = a1, a2, a3
        if a2 is None:
            low, high, step = 0, a1, 1
        elif a3 is None:
            low, high, step = a1, a2, 1
        self.start, self.step = low, step
        if step == 0:
            raise ValueError('xrange() arg 3 must not be zero')
        elif step < 0:
            low, high, step = high, low, -step
        #2.5 self.len = ((high - low - 1) // step + 1) if low < high else 0
        if low < high:
            self.len = ((high - low - 1) // step + 1)
        else:
            self.len = 0

    def __getitem__(self, i):
        if i < 0:
            i += self.len
        if 0 <= i < self.len:
            return self.start + i * self.step       
        raise IndexError("xrange object index out of range")

    def __iter__(self):
        return xrange_iterator(self.start, self.step, self.len)

    def __len__(self):
        return self.len

    def __repr__(self):
        if self.start == 0 and self.step == 1:
            return 'xrange(%d)' % (self.start + self.len * self.step)
        elif self.step == 1:
            return 'xrange(%d, %d)' % (self.start,
                                       self.start + self.len * self.step)
        return 'xrange(%d, %d, %d)' % (self.start,
                                       self.start + self.len * self.step,
                                       self.step)

class xrange_iterator:
    # ??? should this inherit from compact object

    def __init__(self, start, step, len):
        self.start = start
        self.step = step
        self.len = len
        self.index = 0

    def __iter__(self):
        return self

    def next(self):
        if self.index >= self.len:
            raise StopIteration
        result = self.start + self.index * self.step
        self.index += 1
        return result
