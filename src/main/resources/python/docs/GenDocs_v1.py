# Generates patchouli entry JSONs in generated based on txt files in src
# Requires Python 3.9+
# Do not try to use this for unusual entries that don't fit my format- make those manually
# Expects a certain format for the src, detailed below
# Navigates the folder structure
# Assumes the category of an entry is the same as the folder name
# Sets read_by_default to true
# Expects the FIRST LINE of the txt to be the title. If the title ends with a digit, that digit becomes the sortnum. A dash can be placed before the sortnum to prevent priority mode
# Expects the SECOND LINE of the txt to be the item path used for icon and spotlight page
# If the SECOND LINE contains a | (pipe symbol), it will treat everything after the | as an advancement to lock the entry behind
# Expects the THIRD LINE of the txt to be empty, or a comma-separated list of ItemStack strings representing items/blocks to link to this entry.
# If an ItemStack string is followed by a ; and a number, the number will be treated as the line in the body that should be opened. 0-indexed, only counts lines in the body (including blank lines)
# Makes the first page a spotlight, with the entry title
# Will make all other pages text
# Each line in the txt after the first two are considered the body
# The body will be auto-divided into pages
# A new line will be represented as a new paragraph or a new page
# A full empty line will force a new page; it can have <page|TYPE|data0|data1|...> to have a special type for the next page of TYPE, with data parameters passed. The parameters depend on the page- see specific functions below
# For text: <page|text|anchor (opt)>
# For image: <page|image|anchor (opt)|title|image link>
# For spotlight: <page|spotlight|anchor (opt)|item, can have nbt>
# This script will recognize and maintain (incl between pages) the following formatting codes ONLY:
# /$: Clear formatting
# <item> -> Item formatting
# <thing> -> Thing/concept formatting
# <em> -> Italics formatting (used for emphasis)
# <bobo> -> Bobo formatting
# <link:?> -> Link. Replace ? with the link path- anything between <link: and > will be considered the path

import os

def run():
	"""
	Processes all source files which are .txt file format
	:return:
	"""
	outputPath = "../../assets/essentials/patchouli_books/manual/"
	modNamespace = 'essentials'
	srcPath = "../docs/src/"
	templatePath = "../docs/template.txt"
	advTemplatePath = "../docs/template_adv.txt"

	# # Delete all previous files in the outputPath
	# for prevTable in os.listdir(outputPath):
	# 	if os.path.isfile(prevTable):
	# 		os.unlink(prevTable)

	# Read the template
	with open(templatePath, 'r') as fTemp:
		tempLines = fTemp.readlines()
		fTemp.close()
	# Read the adv template
	with open(advTemplatePath, 'r') as fTemp:
		tempAdvLines = fTemp.readlines()
		fTemp.close()

	for viewDir, subDirList, fileList in os.walk(srcPath):
		# We want our directory names to end with /
		viewDir = os.path.normpath(viewDir)
		relPath = os.path.relpath(viewDir, start=srcPath)
		outDir = os.path.join(outputPath, relPath)
		# Create any missing folders
		os.makedirs(outDir, exist_ok=True)

		# Assume category is the same as the folder, including folder structure starting at entries
		category = modNamespace + ':' + viewDir[viewDir.rfind("src") + 18:]

		# Perform conversion for each .txt file
		for viewFile in fileList:
			# Expects src to be txt files
			if viewFile.lower().endswith('.txt'):
				with open(os.path.join(viewDir, viewFile), mode='r', encoding='utf-8') as fIn:
					rawLinesIn = fIn.readlines()
					fIn.close()

				name = rawLinesIn[0][:-1]  # Cut the \n
				# Sortnum can be appended to the end of the name
				sort = '0'
				priority = "false"
				if name[-1].isnumeric():
					sort = name[-1]
					name = name[:-1]
					if name[-1] == '-':
						name = name[:-1]
					else:
						priority = "true"
				icon = rawLinesIn[1][:-1]  # Cut the \n

				# Optional advancement setting
				advancement = ''
				if '|' in icon:
					index = icon.find('|')
					advancement = icon[index + 1:]
					icon = icon[:index]



				(pages, lineStartPageNumbers) = parseBody(rawLinesIn[3:], icon, name, 2)

				# Parse the ItemStack:entry mappings
				mappingsRaw = rawLinesIn[2].strip().split(",")
				mappings = []
				for mapping in mappingsRaw:
					if mapping == "":
						continue

					# page number within entry is optional, defaults 0
					if ';' in mapping:
						mappings.append(mapping.split(';'))
					else:
						mappings.append((mapping, 0))

				mappings = formatMappings(mappings, lineStartPageNumbers, indent=2)

				with open(os.path.join(outDir, viewFile).replace(".txt", ".json"), mode='w+', encoding='utf-8') as fOut:
					fOut.truncate(0)  # Remove previous version

					if len(advancement) == 0:
						for line in tempLines:
							fOut.write(line.replace('CAT', category).replace('NAME', name).replace('ICON', icon).replace('PAGES', pages).replace('SORT', sort).replace("PRIO", priority).replace("MAPPINGS", mappings))
					else:
						for line in tempAdvLines:
							fOut.write(line.replace('CAT', category).replace('NAME', name).replace('ICON', icon).replace('PAGES', pages).replace('SORT', sort).replace("PRIO", priority).replace('ADV', advancement).replace("MAPPINGS", mappings))

					fOut.close()


def parseLine(line: str, size: int, prevFormat: str) -> (str, str, str, int):
	"""
	Parses one text line, pulling words up to size and applies existing format
	:param line: The line
	:param size: The max size of text being pulled, nominally in characters
	:param prevFormat: The existing format, or empty string
	:return: (pulled text, remaining text, text format to carry forward, remaining size
	"""

	boboMult = 1.2  # Bolded 'bobo' text is thicker; multiplier for charcount on bold text
	boboTag = '<bobo>'
	space = ' '
	currPage = prevFormat  # Output text
	currChars = 0  # Used size
	activeFormat = prevFormat  # Currently applied active format

	# We iterate over the entire line one character at a time, and add text to the page one word at a time
	while len(line) != 0:
		wordEndInd = 0
		while wordEndInd < len(line):  # Finds entire words and adds them
			if line[wordEndInd] == space:
				# found the end of a word- add it to the page
				currPage += line[0:wordEndInd + 1]
				line = line[wordEndInd + 1:]
				currChars += (wordEndInd + 1) * (boboMult if boboTag in activeFormat else 1)
				if currChars > size:
					# Filled page
					return (currPage, line, activeFormat, 0)
				break
			elif getFormCode(line[wordEndInd:]):
				# We found a formatting code. We have to do things, and also consider this the end of the word
				code = getFormCode(line[wordEndInd:])

				# found the end of a word- add it to the page
				currPage += line[0:wordEndInd]
				line = line[wordEndInd:]
				currChars += wordEndInd * (boboMult if boboTag in activeFormat else 1)

				if currChars > size:
					# Filled page
					return (currPage, line, activeFormat, 0)

				# Handle the formatting code
				if code in ['<item>', '<thing>', '<em>', '<bobo>']:
					activeFormat += code
					currPage += code
					line = line[len(code):]  # Remove the formatting code from the source line
				elif code == '/$':
					activeFormat = ''
					currPage += '/$'
					line = line[len(code):]  # Remove the formatting code from the source line
				else:
					# This is a link
					# Special casing has to happen for the link path
					endLinkInd = line.find('>')  # Find the close of the link
					linkForm = '$(l:' + line[len(code):endLinkInd]
					linkForm += ')'
					currPage += linkForm
					activeFormat += linkForm
					line = line[endLinkInd + 1:]  # Remove the formatting code from the source line

				break
			else:
				# Did not find the end of the word/formatting code- continue
				wordEndInd += 1
		else:
			# Reached the end without finding the "word end"
			currPage += line
			currChars += len(line) * (boboMult if boboTag in activeFormat else 1)
			line = ''

	return (currPage, line, activeFormat, max(0, size - currChars))


def writeTextPage(output: str, text: str, lineSt: str, data: [str, ...]) -> str:
	"""
	Writes a text page in JSON to output
	:param output: The previous output json
	:param text: The body text to write
	:param lineSt Filler indents at the start of each written line
	:param data Possibly empty list of params, in order [anchor]
	:return: The new output string
	"""
	if len(output) > 0:
		output += ',\n'
	output += lineSt + '{\n'
	output += lineSt + '\t"type": ' + '"patchouli:text",\n'
	if len(data) != 0 and len(data[0]) != 0:
		output += lineSt + '\t"anchor": "' + data[0] + '",\n'
	output += lineSt + '\t"text": "' + text.replace('"', '\\"') + '"\n'
	output += lineSt + '}'
	return output


def writeSpotlightPage(output: str, text: str, lineSt: str, data: [str, ...]) -> str:
	"""
	Writes a spotlight page in JSON to output
	:param output: The previous output json
	:param text: The body text to write
	:param lineSt Filler indents at the start of each written line
	:param data Possibly empty list of params, in order [anchor, item, title]
	:return: The new output string
	"""
	if len(output) > 0:
		output += ',\n'
	output += lineSt + '{\n'
	output += lineSt + '\t"type": ' + '"patchouli:spotlight",\n'
	if len(data) != 0 and len(data[0]) != 0:
		output += lineSt + '\t"anchor": "' + data[0] + '",\n'
	if len(data) > 2 and len(data[2]) != 0:
		output += lineSt + '\t' + '"title": "' + data[2] + '",\n'
	output += lineSt + '\t' + '"item": "' + (data[1] if len(data) > 1 else 'minecraft:stick') + '",\n'
	output += lineSt + '\t"text": "' + text.replace('"', '\\"') + '"\n'
	output += lineSt + '}'
	return output


def writeImagePage(output: str, text: str, lineSt: str, data: [str, ...]) -> str:
	"""
	Writes an image page in JSON to output
	:param output: The previous output json
	:param text: The body text to write
	:param lineSt Filler indents at the start of each written line
	:param data Possibly empty list of params, in order [anchor, title, images...]
	:return: The new output string
	"""
	if len(output) > 0:
		output += ',\n'
	output += lineSt + '{\n'
	output += lineSt + '\t"type": ' + '"patchouli:image",\n'
	if len(data) != 0 and len(data[0]) != 0:
		output += lineSt + '\t"anchor": "' + data[0] + '",\n'
	output += lineSt + '\t"border": "true",\n'
	if len(data) > 1 and len(data[1]) != 0:
		output += lineSt + '\t"title": "' + data[1] + '",\n'
	if len(data) > 2:
		output += lineSt + '\t"images": [\n'
		for i in range(2, len(data)):
			output += lineSt + '\t\t"' + data[i] + '"' + (',' if i != len(data) - 1 else '') + '\n'
		output += lineSt + '\t],\n'
	output += lineSt + '\t"text": "' + text.replace('"', '\\"') + '"\n'
	output += lineSt + '}'
	return output


def writeEntityPage(output: str, text: str, lineSt: str, data: [str, ...]) -> str:
	"""
	Writes an entity page in JSON to output
	:param output: The previous output json
	:param text: The body text to write
	:param lineSt Filler indents at the start of each written line
	:param data Possibly empty list of params, in order [anchor, entity]
	:return: The new output string
	"""
	if len(output) > 0:
		output += ',\n'
	output += lineSt + '{\n'
	output += lineSt + '\t"type": ' + '"patchouli:entity",\n'
	if len(data) != 0 and len(data[0]) != 0:
		output += lineSt + '\t"anchor": "' + data[0] + '",\n'
	if len(data) > 1:
		output += lineSt + '\t' + '"entity": "' + data[1] + '",\n'
	output += lineSt + '\t"text": "' + text.replace('"', '\\"') + '"\n'
	output += lineSt + '}'
	return output


def parseBody(text: [str, ...], icon: str, title: str, indents: int) -> tuple[str, list[int]]:
	"""
	Generates pages from the raw input
	:param text: The list of strings where each string is one body line in the source file
	:param icon: The icon path
	:param title: The title of this entry
	:param indents: The number of indentations to include at minimum on each output line
	:return: A 2-tuple containing: a string (with many new lines) containing the page definitions, and a list of ints
	representing the page numbers that lines of the input ended up on
	"""

	lineSt = '\t' * indents  # Placed at the beginning of every line- for indentation
	output = ''  # Final output string
	pageCharLimit = {'text': 380, 'spotlight': 300, 'image': 80, 'entity': 80}
	charPerNewline = 45  # Number of characters to consider a newline
	jsonWriters = {'text': writeTextPage, 'spotlight': writeSpotlightPage, 'image': writeImagePage, 'entity': writeEntityPage}

	currentPageNumber = 0

	# Line number x in the input starts at the page number given by lineStartPageNumbers[x]
	lineStartPageNumbers = []

	# First page is always a spotlight page
	pageType = 'spotlight'
	pageData = ['', icon, title]

	pageFormat = ''
	pageText = ''
	pageSpaceRemain = pageCharLimit[pageType]

	for line in text:
		lineStartPageNumbers.append(currentPageNumber)
		if line == '\n' or line == '':
			# Empty line. Treat as <page:text>
			line = '<page|text>\n'
		if line.startswith('<page|'):
			# This line forces a new page, and does not contain body text
			# Create page with previous text
			output = jsonWriters[pageType](output, pageText, lineSt, pageData)
			pageText = ''
			currentPageNumber += 1

			# Parse data for following page
			parts = [x if x is not None else '' for x in line[6:-2].split('|')]  # Sanitize the input, replace None entries with empty string
			pageType = parts[0]
			pageData = [] if len(parts) < 2 else parts[1:]
			pageSpaceRemain = pageCharLimit[pageType]
		else:
			# Body text

			# Check for page length due to previous line adding line breaks
			if pageSpaceRemain <= 0:
				output = jsonWriters[pageType](output, pageText, lineSt, pageData)
				pageText = ''
				pageType = 'text'
				pageData = []
				pageSpaceRemain = pageCharLimit[pageType]
				currentPageNumber += 1

			# Add text from the line, and divide the line into pages until finished
			lineText = line
			if lineText[-1] == '\n':
				# Trim the newline character
				lineText = lineText[:-1]

			while len(lineText) > 0:
				(newPageText, lineText, pageFormat, pageSpaceRemain) = parseLine(lineText, pageSpaceRemain, pageFormat)
				pageText += newPageText
				if pageSpaceRemain <= 0:
					# Add page and reset
					output = jsonWriters[pageType](output, pageText, lineSt, pageData)
					pageText = ''
					pageType = 'text'
					pageData = []
					pageSpaceRemain = pageCharLimit[pageType]
					currentPageNumber += 1

			# Add paragraph break- the next line will do the check for if this forces a new page
			if pageText != '':
				pageText += '$(br2)'
				pageSpaceRemain -= charPerNewline
	else:
		# Ended final line, add any remaining text as a final page
		if len(pageText) > 0 or pageType != 'text':
			output = jsonWriters[pageType](output, pageText, lineSt, pageData)
			pageText = ''
			pageType = 'text'
			pageData = []
			pageSpaceRemain = pageCharLimit[pageType]

	return (output, lineStartPageNumbers)


def formatMappings(mappings: list[tuple[str, int]], lineStartPageNumbers: list[int], indent: int = 0) -> str:
	"""
	Generates the formatted item -> entry mappings from some lightly parsed input
	:param mappings:
	:param indent:
	:return:
	"""
	leadingTabs = '\t' * indent

	out = f",\n{leadingTabs}".join(f"\"{mapping[0]}\" : {lineStartPageNumbers[int(mapping[1])]}" for mapping in mappings)

	return out


def getFormCode(line: str) -> str:
	formatCodes = {'<item>', '<thing>', '<em>', '<bobo>', '/$', '<link:'}  # Formatting codes to check for
	for code in formatCodes:
		if line.startswith(code):
			return code
	return ''


