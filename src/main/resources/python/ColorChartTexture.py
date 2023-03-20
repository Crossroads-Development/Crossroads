# Updates the color_chart_gui.png file to have a section for each beam alignment, with only that alignment colored in
# Must be updated and run whenever EnumBeamAlignments has the color->alignment mappings changed

from PIL import (
    Image,
)  # You'll probably need to install 'pillow', as the original 'PIL' is discontinued
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
    ((255, 0, 0), 254),
]

# Size of one color chart gui
x_scale = 300
y_scale = 318
radius = 138  # Radius of the color wheel
# Center position of the color wheel
x_center = x_scale / 2
y_center = 150


def getOrdinal(xPos: int, yPos: int) -> int:
    # Convert pos -> HSV -> RGB -> ordinal
    # centre = scale / 2
    distFromCentre = math.sqrt((xPos - x_center) ** 2 + (yPos - y_center) ** 2)
    if distFromCentre > radius:
        return -1
    (r, g, b) = colorsys.hsv_to_rgb(
        (math.atan2(yPos - y_center, xPos - x_center) / (2 * math.pi) + 1.0) % 1.0,
        distFromCentre / radius,
        1,
    )
    (r, g, b) = (255 * r, 255 * g, 255 * b)
    for i in range(0, len(alignments)):
        alignCol = alignments[i][0]
        dist = alignments[i][1]
        if (
            abs(r - alignCol[0]) < dist
            and abs(g - alignCol[1]) < dist
            and abs(b - alignCol[2]) < dist
        ):
            return i
    return -1


textPath = "../assets/crossroads/textures/gui/container/color_chart_gui.png"

image = Image.open(textPath)
x_grid_size = image.size[0] / x_scale  # Number of gui images per row
y_grid_size = image.size[1] / y_scale  # Number of gui images per column
pixels = image.load()

for x in range(0, x_scale):
    for y in range(0, y_scale):
        ordinal = getOrdinal(x, y)
        fromPixel = image.getpixel((x, y))
        if ordinal >= 0:
            # Copy this pixel to the corresponding section for that alignment
            ordinal += 2  # Offset from the first two slots, which are taken
            endPos = (
                (ordinal % x_grid_size) * x_scale + x,
                (ordinal // x_grid_size) * y_scale + y,
            )
            pixels[endPos[0], endPos[1]] = fromPixel

image.save(textPath)  # Overwrite existing image
