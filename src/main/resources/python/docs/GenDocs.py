# Generates patchoulli entry JSONs in generated based on txt files in src
# Do not try to use this for unusual entries that don't fit my format- make those manually
# Expects a certain format for the src, detailed below
# Navigates the folder structure
# Assumes the category of an entry is the same as the folder name
# Sets read_by_default to true
# Expects the FIRST LINE of the txt to be the title. If the title ends with a digit, that digit becomes the sortnum
# Expects the SECOND LINE of the txt to be the item path used for icon and spotlight page
# Makes the first page a spotlight, with the entry title
# Will make all other pages text
# Each line in the txt after the first two are considered the body
# The body will be auto-divided into pages
# A new line will be represented as a new paragraph or a new page
# A full empty line will force a new page
# This script will recognize and maintain (incl between pages) the following formatting codes ONLY:
# /$: Clear formatting
# <item> -> Item formatting
# <thing> -> Thing/concept formatting
# <em> -> Italics formatting (used for emphasis)
# <bobo> -> Bobo formatting
# <link:?> -> Link. Replace ? with the link path- anything between <link: and > will be considered the path

import os


def run():
	outputPath = "../docs/generated/"
	srcPath = "../docs/src/"
	templatePath = "../docs/template.txt"

	# Delete all previous files in the outputPath
	for prevTable in os.listdir(outputPath):
		if os.path.isfile(prevTable):
			os.unlink(prevTable)

	# Read the template
	with open(templatePath, 'r') as fTemp:
		tempLines = fTemp.readlines()
		fTemp.close()

	for inDir, subDirList, fileList in os.walk(srcPath):
		# Copy folder structure for generated

		# We want our directory names to end with /
		if inDir[-1] != '/':
			inDir = inDir + '/'

		outDir = inDir.replace('\\', '/').replace("/src", "/generated")
		# Create any missing folders
		os.makedirs(outDir, exist_ok=True)

		# Assume category is the same as the folder, including folder structure starting at generated
		category = outDir[outDir.rfind("generated/") + 10:-1]

		# Perform conversion for each .txt file
		for file in fileList:
			# Expects src to be txt files
			if '.txt' in file:
				with open(inDir + file, 'r') as fIn:
					rawLinesIn = fIn.readlines()
					fIn.close()

				name = rawLinesIn[0][:-1]  # Cut the \n
				icon = rawLinesIn[1][:-1]  # Cut the \n
				pages = generatePages(rawLinesIn, icon, name, 2)
				sort = '0'
				# Sortnum can be appended to the end of the name
				if name[-1].isnumeric():
					sort = name[-1]
					name = name[:-1]


				with open(outDir + file.replace(".txt", ".json"), 'w+') as fOut:
					for line in tempLines:
						fOut.write(line.replace('CAT', category).replace('NAME', name).replace('ICON', icon).replace('PAGES', pages).replace('SORT', sort))
					fOut.close()


def generatePages(text: list, icon: str, title: str, indents: int) -> str:
	"""
	Generates pages from the raw input
	:param text: The list of strings where each string is one line in the source file
	:param icon: The icon path
	:param title: The title of this entry
	:param indents: The number of indentations to include at minimum on each output line
	:return: A string (with many new lines) containing the page definitions
	"""

	lineSt = '\t' * indents  # Placed at the beginning of every line- for indentation
	output = ''  # Final output string
	charLimit = 480  # Maximum characters per text page
	charLimitTitle = 330  # Maximum characters per title page
	charPerNewline = 45  # Number of characters to consider a newline
	space = ' '

	pages = []

	activeFormat = ''  # Current active formatting codes being applied- used to continue onto next page
	currPage = ''
	currChars = 0  # Number of chars on current page, not counting formatting codes

	lineNum = 2  # Start at line 3, after name and icon spec
	lineCount = len(text)
	forcedPage = False
	while lineNum < lineCount:
		if lineNum != 2:
			# This is a new line. We need to check for either a break or new paragraph
			if len(text[lineNum]) <= 1:
				# Force a new page (empty line)
				pages.append(currPage)
				currPage = activeFormat  # Continue our current formatting
				currChars = 0
				lineNum += 1
				forcedPage = True
				continue
			elif not forcedPage:
				# Force a new paragraph, or a new page
				currChars += charPerNewline
				if currChars > (charLimitTitle if len(pages) == 0 else charLimit):
					# New page
					pages.append(currPage)
					currPage = activeFormat  # Continue our current formatting
					currChars = 0
				else:
					# Append an empty line
					# Note the escaped backslashes
					# We want \n to be written in the JSON, which the JSON itself will interpret as a newline
					currPage += '$(br2)'
		forcedPage = False

		# Add the contents of this line, divided into pages

		line = text[lineNum]
		if line[-1] == '\n':
			line = line[:-1]  # Remove the newline

		# We iterate over the entire line one character at a time, and add text to the page one word at a time
		while len(line) != 0:
			wordEndInd = 0
			while wordEndInd < len(line):  # Finds entire words and adds them
				if line[wordEndInd] == space:
					# found the end of a word- add it to the page
					currPage += line[0:wordEndInd + 1]
					line = line[wordEndInd + 1:]
					currChars += wordEndInd + 1

					if currChars > (charLimitTitle if len(pages) == 0 else charLimit):
						# New page
						pages.append(currPage)
						currPage = activeFormat  # Continue our current formatting
						currChars = 0
					break
				elif getFormCode(line[wordEndInd:]):
					# We found a formatting code. We have to do things, and also consider this the end of the word
					code = getFormCode(line[wordEndInd:])

					# found the end of a word- add it to the page
					currPage += line[0:wordEndInd]
					line = line[wordEndInd:]
					currChars += wordEndInd

					if currChars > (charLimitTitle if len(pages) == 0 else charLimit):
						# New page
						pages.append(currPage)
						currPage = activeFormat  # Continue our current formatting
						currChars = 0

					# Handle the formatting code
					if code in ['<item>', '<thing>', '<em>', '<bobo>']:
						activeFormat += code
						currPage += code
						line = line[len(code):]  # Remove the formatting code from the source line
					elif code == '/$':
						currPage += '/$'
						activeFormat = ''
						line = line[len(code):]  # Remove the formatting code from the source line
					else:
						# This is a link
						# Special casing has to happen for the link path
						endLinkInd = line.find('>')  # Find the close of the link
						format = '$(l:' + line[len(code):endLinkInd]
						format += ')'
						currPage += format
						activeFormat += format
						line = line[endLinkInd + 1:]  # Remove the formatting code from the source line

					break
				else:
					# Did not find the end of the word/formatting code- continue
					wordEndInd += 1
			else:
				# Reached the end without finding the "word end"
				currPage += line
				currChars += len(line)
				line = ''

				if currChars > (charLimitTitle if len(pages) == 0 else charLimit):
					# New page
					pages.append(currPage)
					currPage = activeFormat  # Continue our current formatting
					currChars = 0

		lineNum += 1  # Move to the next line

	if len(currPage) != 0:
		# Add any remaining text from the last line
		pages.append(currPage)

	# Convert the pages into a single string with newlines
	first = True  # Whether this is the first page
	for page in pages:
		if not first:
			output += ',\n'
		output += lineSt + '{\n'
		output += lineSt + '\t"type": ' + ('"spotlight",\n' if first else '"text",\n')
		if first:
			output += lineSt + '\t' + '"title": "' + title + '",\n'
			output += lineSt + '\t' + '"item": "' + icon + '",\n'
		output += lineSt + '\t"text": "' + page + '"\n'
		output += lineSt + '}'
		first = False

	return output


def getFormCode(line: str) -> str:
	formatCodes = {'<item>', '<thing>', '<em>', '<bobo>', '/$', '<link:'}  # Formatting codes to check for
	for code in formatCodes:
		if line.startswith(code):
			return code
	return ''


# Actually runs the script
run()
