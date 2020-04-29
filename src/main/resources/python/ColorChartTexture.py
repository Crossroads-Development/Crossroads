# Updates the color_chart_gui.png file to have a section for each beam alignment, with only that alignment colored in
# Must be updated and run whenever EnumBeamAlignments has the color->alignment mappings changed

from PIL import Image
import colorsys
import math

# Alignment definitions, in same order as in EnumBeamAlignments by ordinal
# ((r, g, b), range)
alignments = [
	((255, 100, 0), 16),
	((251, 255, 184), 16),
	((255, 132, 255), 40),
	((255, 0, 255), 96),
	((255, 255, 0), 128),
	((0, 255, 255), 72),
	((132, 255, 255), 64),
	((255, 255, 255), 128),
	((0, 0, 255), 254),
	((0, 255, 0), 254),
	((255, 0, 0), 254)
]

scale = 300  # Size of one color chart gui
radius = 138  # Radius of the color wheel


def getOrdinal(xPos: int, yPos: int) -> int:
	# Convert pos -> HSV -> RGB -> ordinal
	centre = scale / 2
	distFromCentre = math.sqrt((xPos - centre) ** 2 + (yPos - centre) ** 2)
	if distFromCentre > radius:
		return -1
	(r, g, b) = colorsys.hsv_to_rgb((math.atan2(yPos - centre, xPos - centre) / (2 * math.pi) + 1.0) % 1.0, distFromCentre / radius, 1)
	(r, g, b) = (255 * r, 255 * g, 255 * b)
	for i in range(0, len(alignments)):
		alignCol = alignments[i][0]
		dist = alignments[i][1]
		if abs(r - alignCol[0]) < dist and abs(g - alignCol[1]) < dist and abs(b - alignCol[2]) < dist:
			return i
	return -1


textPath = '../assets/crossroads/textures/gui/container/color_chart_gui.png'

image = Image.open(textPath)
gridSize = image.size[0] / scale  # Number of gui images per row/column
pixels = image.load()

for x in range(0, scale):
	for y in range(0, scale):
		ordinal = getOrdinal(x, y)
		fromPixel = image.getpixel((x, y))
		if ordinal >= 0:
			# Copy this pixel to the corresponding section for that alignment
			ordinal += 2  # Offset from the first two slots, which are taken
			endPos = ((ordinal % gridSize) * scale + x, (ordinal // gridSize) * scale + y)
			pixels[endPos[0], endPos[1]] = fromPixel

image.save(textPath)  # Overwrite existing image
