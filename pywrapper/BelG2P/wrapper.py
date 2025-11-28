import jpype
import jpype.imports
from jpype.types import *
import os

def start_jvm():
    if jpype.isJVMStarted():
        return

    base = os.path.dirname(__file__)
    jar_path1 = os.path.join(base, "jvm", "BelG2P-with-dependencies.jar")
    jar_path2 = os.path.join(base, "jvm", "g.jar")

    # Запускаем JVM
    jpype.startJVM(
        classpath=[jar_path1,jar_path2],
        convertStrings=True
    )


class BelG2PWrapper:
    def __init__(self):
        start_jvm()

        from org.alex73.fanetyka.impl import FanetykaConfig
        from org.alex73.grammardb import GrammarDB2
        from org.alex73.grammardb import GrammarFinder
        from org.alex73.fanetyka.impl.str import ToStringIPA2TTS

        db = GrammarDB2.initializeFromJar();
        finder = GrammarFinder(db)
        self.config = FanetykaConfig(finder)
        self.outType = ToStringIPA2TTS()

    def convert(self, words: str):
        from org.alex73.fanetyka.impl import Fanetyka3
        from java.util import ArrayList

        f = Fanetyka3(self.config)
        jwords = ArrayList(words.split())
        f.calcFanetyka(jwords)
        return f.toString(self.outType)
