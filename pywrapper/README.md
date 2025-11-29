Python wrapper around BelG2P
============================

Зборка модуля:

	python3 -m venv ../venv
	../venv/bin/pip3 install build twine
	../venv/bin/pip3 install .
	../venv/bin/python3    # праверыць прыклад выкарыстання
	rm dist/*
	../venv/bin/python3 -m build
	../venv/bin/twine upload dist/*

Прыклад выкарыстання:

	from BelG2P import BelG2PWrapper
	
	g2p=BelG2PWrapper()
	print(g2p.convert("нейкія словы можна пісаць тут"))
