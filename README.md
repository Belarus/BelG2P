Фанетычны канвертар беларускай мовы
===================================

Фанетычны канвертар канвертуе беларускія словы ў іх фанетычнае прадстаўленне.

Для працы патрабуецца [Граматычная база](https://github.com/Belarus/GrammarDB) (звесткі і jar).

Выкарыстанне канвертара:

	var db = GrammarDB2.initializeFromJar();  // чытаць Граматычную базу беларускай мовы
	var finder = new GrammarFinder(db);       // стварыць пошукавік па Граматычнай базе
	var config = new FanetykaConfig(finder);  // стварыць канфігурацыю на падставе пошукавіку і ўбудаваных табліц канвертара
	var f = new Fanetyka3(config);            // стварыць канвертар
	f.calcFanetyka(words);                    // канвертаванне
	f.toString(new ToStringIPA());            // вывад фанетыкі ў патрэбным стандарце

GrammarDB2, GrammarFinder, FanetykaConfig - thread safe.

Fanetyka3 - не thread safe.

Файлы
-----

nienacisknyja.txt - з якіх слоў прыбіраецца націск

prystauki.txt - прыстаўкі, якія распаўсюджваюцца на незнаёмыя словы
