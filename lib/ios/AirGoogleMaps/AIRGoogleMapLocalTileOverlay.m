//
//  AIRGoogleMapRealLocalTile.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import "AIRGoogleMapLocalTileOverlay.h"

static NSArray<PresetColor *> *magmaPreset = nil;

@implementation AIRGoogleMapLocalTileOverlay

- (instancetype)init
{
    self = [super init];
    if (self) {
        magmaPreset = [NSArray arrayWithObjects:
                       [PresetColor makeWithPercent:0 color:[SimpleColor makeWithR:1 g:1 b:6]],
                       [PresetColor makeWithPercent:0.25 color:[SimpleColor makeWithR:72 g:20 b:97]],
                       [PresetColor makeWithPercent:0.5 color:[SimpleColor makeWithR:176 g:47 b:76]],
                       [PresetColor makeWithPercent:0.75 color:[SimpleColor makeWithR:243 g:109 b:24]],
                       [PresetColor makeWithPercent:1.0 color:[SimpleColor makeWithR:249 g:251 b:147]],
                       nil
                       ];
    }
    return self;
}

- (void)setFileTemplate:(NSString *)fileTemplate {
    _fileTemplate = fileTemplate;
}

- (void)setUrlTemplate:(NSString *)urlTemplate {
    _urlTemplate = urlTemplate;
}

- (void)setMaxTempRange:(NSArray *)maxTempRange {
    _maxTempRange = maxTempRange;
}

- (void)setCurrentTempRange:(NSArray *)currentTempRange {
    _currentTempRange = currentTempRange;
}

- (void)requestTileForX:(NSUInteger)x y:(NSUInteger)y zoom:(NSUInteger)zoom receiver:(id<GMSTileReceiver>)receiver {
    NSURL *urlFilePath = [self urlFilePathForZ:zoom x:x y:y];
    if ([[NSFileManager defaultManager] fileExistsAtPath:urlFilePath.path]) {
        [receiver receiveTileWithX:x y:y zoom:zoom image:[self processImage:[UIImage imageWithContentsOfFile:urlFilePath.path]]];
    } else if (self.urlTemplate) {
        NSData *urlImageData = [NSData dataWithContentsOfURL:[self urlInternetPathForZ:zoom x:x y:y]];
        [receiver receiveTileWithX:x y:y zoom:zoom image:[self processImage:[UIImage imageWithData:urlImageData]]];
    } else {
        [receiver receiveTileWithX:x y:y zoom:zoom image:kGMSTileLayerNoTile];
    }
}

- (NSURL *)urlInternetPathForZ:(NSUInteger)z x:(NSUInteger)x y:(NSUInteger)y {
    return [NSURL URLWithString:[self parsePath:self.urlTemplate z:z x:x y:y]];
}

- (NSURL *)urlFilePathForZ:(NSUInteger)z x:(NSUInteger)x y:(NSUInteger)y {
    return [NSURL URLWithString:[self parsePath:self.fileTemplate z:z x:x y:y]];
}

- (NSString *)parsePath:(NSString *)path z:(NSUInteger)z x:(NSUInteger)x y:(NSUInteger)y {
    NSString *newPath = path;
    newPath = [newPath stringByReplacingOccurrencesOfString:@"{x}" withString:[NSString stringWithFormat: @"%ld", (long)x]];
    newPath = [newPath stringByReplacingOccurrencesOfString:@"{y}" withString:[NSString stringWithFormat: @"%ld", (long)(1 << z) - 1 - y]];
    newPath = [newPath stringByReplacingOccurrencesOfString:@"{z}" withString:[NSString stringWithFormat: @"%ld", (long)z]];
    return newPath;
}

-(UIImage *)processImage:(UIImage *)image {
    if (!self.maxTempRange || !self.currentTempRange || !image) return image;
    NSNumber *minTemp = self.maxTempRange[0];
    NSNumber *maxTemp = self.maxTempRange[1];
    NSNumber *currentMinTemp = self.currentTempRange[0];
    NSNumber *currentMaxTemp = self.currentTempRange[1];

    // Convert UIImage to CGImage, extract width & height + allocate pixel array.
    CGImageRef cgImage = image.CGImage;
    size_t width = CGImageGetWidth(cgImage);
    size_t height = CGImageGetHeight(cgImage);
    unsigned char *pixels = malloc(width*height*4);
    // Create color space and create a CGContext + release color space
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(pixels, width, height, 8, 4*width, colorSpace, kCGImageAlphaPremultipliedLast);
    CGColorSpaceRelease(colorSpace);

    CGContextDrawImage(context, CGRectMake(0, 0, width, height), cgImage);

    for (int y=0;y<height;++y){
        for (int x=0;x<width;++x){
            long idx = (width*y+x)*4; //the index of pixel(x,y) in the 1d array pixels
            uint red = pixels[idx];
            uint green = pixels[idx+1];
            uint blue = pixels[idx+2];
            uint alpha = pixels[idx+3];

            if (alpha == 0) {
                pixels[idx] = 0;
                pixels[idx+1] = 0;
                pixels[idx+2] = 0;
            } else {
                double step = ([maxTemp doubleValue] - [minTemp doubleValue]) / (256 * 256);
                double elevation = [minTemp doubleValue] + (red * 256 + green + blue) * step;
                unsigned char *colors;
                if (elevation < [currentMinTemp doubleValue]) {
                    colors = [self _getColorForPercentage:0];
                } else if (elevation > [currentMaxTemp doubleValue]) {
                    colors = [self _getColorForPercentage:1];
                } else {
                    double ratio = (elevation - [currentMinTemp doubleValue]) / ([currentMaxTemp doubleValue] - [currentMinTemp doubleValue]);
                    colors = [self _getColorForPercentage:ratio];
                }
                pixels[idx] = colors[0];
                pixels[idx+1] = colors[1];
                pixels[idx+2] = colors[2];
            }
        }
    }

    cgImage = CGBitmapContextCreateImage(context);
    CGContextRelease(context);
    free(pixels);

    return [UIImage imageWithCGImage:cgImage];
}

- (unsigned char *)_getColorForPercentage:(double)percent {
    unsigned char *colors = malloc(3);
    int index = 1;
    for (index = 1; index < [magmaPreset count] - 1; index++) {
        if (percent < magmaPreset[index].percent) break;
    }
    PresetColor *lower = magmaPreset[index - 1];
    PresetColor *upper = magmaPreset[index];
    double rangePercent = (percent - lower.percent) / (upper.percent - lower.percent);
    double percentLower = 1 - rangePercent;
    colors[0] = floor(lower.color.r * percentLower + upper.color.r * rangePercent);
    colors[1] = floor(lower.color.g * percentLower + upper.color.g * rangePercent);
    colors[2] = floor(lower.color.b * percentLower + upper.color.b * rangePercent);
    return colors;
}

@end
