###########################################################################
#
#  Support code for the 'psyco.compact' type.

from UserDict import DictMixin
from _psyco import compact


class compactdictproxy(DictMixin):

    def __init__(self, ko):
        self._ko = ko    # compact object of which 'self' is the dict

    def __getitem__(self, key):
        return compact.__getslot__(self._ko, key)

    def __setitem__(self, key, value):
        compact.__setslot__(self._ko, key, value)

    def __delitem__(self, key):
        compact.__delslot__(self._ko, key)

    def keys(self):
        return compact.__members__.__get__(self._ko)

    def clear(self):
        keys = self.keys()
        keys.reverse()
        for key in keys:
            del self[key]

    def __repr__(self):
        keys = ', '.join(self.keys())
        return '<compactdictproxy object {%s}>' % (keys,)
