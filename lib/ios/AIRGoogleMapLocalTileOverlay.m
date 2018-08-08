//
//  AIRGoogleMapRealLocalTile.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import "AIRGoogleMapLocalTileOverlay.h"

@implementation AIRGoogleMapLocalTileOverlay

- (void)setFileTemplate:(NSString *)fileTemplate {
    _fileTemplate = fileTemplate;
}

- (void)setUrlTemplate:(NSString *)urlTemplate {
    _urlTemplate = urlTemplate;
}

- (void)setTempRange:(NSArray *)tempRange {
    _tempRange = tempRange;
}

- (void)setCurrentTempRange:(NSArray *)currentTempRange {
    _currentTempRange = currentTempRange;
}

- (void)requestTileForX:(NSUInteger)x y:(NSUInteger)y zoom:(NSUInteger)zoom receiver:(id<GMSTileReceiver>)receiver {
    printf("/%i/%i/%i\n", (unsigned int)zoom, (unsigned int)x, (unsigned int)y);
    NSURL *urlFilePath = [self urlFilePathForZ:zoom x:x y:y];
    if ([[NSFileManager defaultManager] fileExistsAtPath:urlFilePath.path]) {
        printf("Local");
        [receiver receiveTileWithX:x y:y zoom:zoom image:[self processImage:[UIImage imageWithData:[NSData dataWithContentsOfFile:urlFilePath.path]]]];
    } else if (self.urlTemplate) {
        printf("Internet");
        NSData *urlImageData = [NSData dataWithContentsOfURL:[self urlInternetPathForZ:zoom x:x y:y]];
        [receiver receiveTileWithX:x y:y zoom:zoom image:[self processImage:[UIImage imageWithData:urlImageData]]];
    } else {
        printf("No tile");
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
    if (!self.tempRange || !self.currentTempRange) return image;
    NSNumber *minTemp = self.tempRange[0];
    NSNumber *maxTemp = self.tempRange[1];
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

            if (minTemp == nil || maxTemp == nil || blue == 0) {
                pixels[idx] = 0;
                pixels[idx+1] = 0;
                pixels[idx+2] = 0;
            } else {
                long step = ([maxTemp longValue] - [minTemp longValue]) / (256 * 256);
                long elevation = [minTemp longValue] + (red * 256 + green + blue) * step;
                unsigned char *colors;
                if (elevation < [currentMinTemp longValue]) {
                    colors = [self getColorForPercentage:0];
                } else if (elevation > [currentMaxTemp longValue]) {
                    colors = [self getColorForPercentage:1];
                } else {
                    float ratio = (elevation - [currentMinTemp longValue]) / ([currentMaxTemp longValue] - [currentMinTemp longValue]);
                    colors = [self getColorForPercentage:ratio];
                }
                pixels[idx] = colors[0];
                pixels[idx+1] = colors[1];
                pixels[idx+2] = colors[2];
            }
        }
    }

    cgImage = CGBitmapContextCreateImage(context); //create a CGIMageRef from our pixeldata
    //release the drawing env and pixel data
    CGContextRelease(context);
    free(pixels);

    //load our new image
    return [UIImage imageWithCGImage:cgImage];
}

- (NSArray<PresetColor *> *)getMagmaPreset {
    return [NSArray arrayWithObjects:
        [PresetColor makeWithPercent:0 color:[SimpleColor makeWithR:1 g:1 b:6]],
        [PresetColor makeWithPercent:0.25 color:[SimpleColor makeWithR:72 g:20 b:97]],
        [PresetColor makeWithPercent:0.5 color:[SimpleColor makeWithR:176 g:47 b:76]],
        [PresetColor makeWithPercent:0.75 color:[SimpleColor makeWithR:243 g:109 b:24]],
        [PresetColor makeWithPercent:1.0 color:[SimpleColor makeWithR:249 g:251 b:147]],
        nil
    ];
}

- (unsigned char *)getColorForPercentage:(float)percent {
    unsigned char *colors = malloc(3);
    NSArray<PresetColor *> *magma = [self getMagmaPreset];
    int index = 1;
    for (int i = 1; i < [magma count]; i++) {
        if (percent < magma[i].percent) {
            index = i;
            break;
        }
    }
    PresetColor *lower = magma[index - 1];
    PresetColor *upper = magma[index];
    float rangePercent = (percent - lower.percent) / (upper.percent - lower.percent);
    float percentLower = 1 - rangePercent;
    float percentUpper = rangePercent;
    colors[0] = floor(lower.color.r * percentLower + upper.color.r * percentUpper);
    colors[1] =  floor(lower.color.g * percentLower + upper.color.g * percentUpper);
    colors[2] = floor(lower.color.b * percentLower + upper.color.b * percentUpper);
    return colors;
}

@end
