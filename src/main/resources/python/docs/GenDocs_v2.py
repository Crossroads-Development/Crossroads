# Generates patchouli entry JSONs based on json files in src
# Requires Python 3.9+
# MUST HAVE "format": 2 in the JSON to be recognized
# Format: Normal Patchouli entry/category format (see: https://vazkiimods.github.io/Patchouli/docs/reference/entry-json)
# Pages with text will automatically be broken up into multiple pages if the text is too long to fit
# This script will recognize and maintain (incl between pages) the following formatting codes ONLY:
# /$: Clear formatting
# \n: Newline (\n\n is a new paragraph)
# <item> -> Item formatting
# <thing> -> Thing/concept formatting
# <em> -> Italics formatting (used for emphasis)
# <bobo> -> Bobo formatting
# <link:?> -> Link. Replace ? with the link path- anything between <link: and > will be considered the path

import os
import json


def run():
	"""
	Processes all source files in .json file format and have "format": 2
	:return:
	"""

	srcPath = '../docs/src/'
	outputPath = "../../assets/essentials/patchouli_books/manual/"

	for viewDir, subDirList, fileList in os.walk(srcPath):
		# We want our directory names to end with /
		viewDir = os.path.normpath(viewDir)
		relPath = os.path.relpath(viewDir, start=srcPath)
		outDir = os.path.join(outputPath, relPath)
		os.makedirs(outDir, exist_ok=True)
		for viewFile in fileList:
			if viewFile.lower().endswith('.json'):
				with open(os.path.join(viewDir, viewFile), mode='r', encoding='utf-8') as fIn:
					print(f'{viewDir}\\{viewFile}')
					result = processFile(fIn.read())
					if result:
						with open(os.path.join(outDir, viewFile), mode='w+', encoding='utf-8') as fOut:
							fOut.write(result)



def processFile(fileText: str):
	"""
	Processes the contents of one file.
	:param fileText: The entirety of the filetext, as a string
	:return: None if this script is not allowed to handle this file, or a string of the processed file
	"""
	try:
		srcJson = json.loads(fileText)
		if not isinstance(srcJson, dict):
			raise json.JSONDecodeError(msg='Not a valid JSON', doc=fileText, pos=0)
	except json.JSONDecodeError as e:
		print('Unable to decode due to parsing error; skipping')
		print(e)
		return None

	if 'format' in srcJson and (srcJson['format'] == 2 or srcJson['format'] == '2'):
		resultJson = dict(srcJson)
		del resultJson['format']

		# Split up long text on pages into additional text pages
		if 'pages' in srcJson:
			pages = srcJson['pages']
			newPages = []
			pageNumberMap = dict()  # Page # in src -> First page # in result

			for pageNum in range(len(pages)):
				page = pages[pageNum]
				pageNumberMap[pageNum] = len(newPages)
				pageType = page['type']
				if 'text' in page:
					pageText = page['text']
					pagesText = parsePageText(pageText, pageType)
					page['text'] = pagesText[0]
					newPages.append(page)
					# Add the remaining text as new text pages
					del pagesText[0]
					for newPageText in pagesText:
						newPage = {'type': 'patchouli:text', 'text': newPageText}
						# Copy attributes from the original page to the child pages
						if 'flag' in page:
							newPage['flag'] = page['flag']
						if 'advancement' in page:
							newPage['advancement'] = page['advancement']
						newPages.append(newPage)
			resultJson['pages'] = newPages

			if 'extra_recipe_mappings' in srcJson:
				# Correct page numbers on extra recipe mappings
				newMappings = dict()
				for key, value in srcJson['extra_recipe_mappings'].items():
					newMappings[key] = pageNumberMap[int(value)]
				resultJson['extra_recipe_mappings'] = newMappings

		return json.dumps(resultJson, indent='\t', ensure_ascii=False)

	# Incorrect format
	return None


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
	charPerNewline = 23  # Number of characters to consider a newline

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
				elif code == r'\n':
					# newline
					currPage += code
					line = line[len(code):]  # Remove the formatting code from the source line
					currChars += charPerNewline
				else:
					# This is a link
					# Special casing has to happen for the link path
					endLinkInd = line.find(')') if code == '$(l:' else line.find('>')  # Find the close of the link
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


def parsePageText(srcPageText: str, sourcePageType: str) -> list[str]:
	"""
	Splits text on a page into multiple pages
	:param srcPageText: Text to split up
	:param sourcePageType: Type of the source page
	:return: List of page text, one page per entry. First entry corresponds to pageType, remainder assume text pages
	"""

	pages = []
	pageCharLimit = {'patchouli:text': 380, 'patchouli:spotlight': 300, 'patchouli:image': 80, 'patchouli:entity': 80}
	fallbackCharLimit = 300

	pageType = sourcePageType

	pageFormat = ''
	pageSpaceRemain = pageCharLimit[pageType] if pageType in pageCharLimit else fallbackCharLimit

	# Add text from the line, and divide the line into pages until finished
	lineText = srcPageText
	if len(lineText) != 0 and lineText[-1] == '\n':
		# Trim the newline character
		lineText = lineText[:-1]

	pageText = ''
	while len(lineText) > 0:
		(newPageText, lineText, pageFormat, pageSpaceRemain) = parseLine(lineText, pageSpaceRemain, pageFormat)
		pageText += newPageText
		if pageSpaceRemain <= 0:
			# Add page and reset
			pages.append(pageText)
			pageText = ''
			pageType = 'patchouli:text'
			pageSpaceRemain = pageCharLimit[pageType] if pageType in pageCharLimit else fallbackCharLimit

	# Ended final line, add any remaining text as a final page
	if len(pageText) > 0 or pageType != 'text':
		pages.append(pageText)
		pageText = ''
		pageType = 'text'
		pageSpaceRemain = pageCharLimit[pageType] if pageType in pageCharLimit else fallbackCharLimit

	return pages


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
	formatCodes = {'<item>', '<thing>', '<em>', '<bobo>', '/$', '<link:', r'\n', '$(l:'}  # Formatting codes to check for
	for code in formatCodes:
		if line.startswith(code):
			return code
	return ''


