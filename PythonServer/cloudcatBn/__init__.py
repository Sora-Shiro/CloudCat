import codecs
import sys

codecs.register(lambda name: codecs.lookup('utf8') if name == 'utf8mb4' else None)

# Encode is important
default_encoding = 'utf8'
if sys.getdefaultencoding() != default_encoding:
    reload(sys)
    sys.setdefaultencoding(default_encoding)

