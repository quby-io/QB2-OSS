###################################################################################################
# Register pure python function substitutes
# for builtin c functions

from psyco import proxy

from _psyco import register_cfunction2purepython as register

@register
@proxy
def PsySequence_Contains(seq, ob):
    for any in seq:
        if any == ob:
            return True
    return False
