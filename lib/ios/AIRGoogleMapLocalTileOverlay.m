//
//  AIRGoogleMapRealLocalTile.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import "AIRGoogleMapLocalTileOverlay.h"

@implementation AIRGoogleMapLocalTileOverlay

- (void)setPathTemplate:(NSString *)pathTemplate {
    _pathTemplate = pathTemplate;
}

- (void)requestTileForX:(NSUInteger)x y:(NSUInteger)y zoom:(NSUInteger)zoom receiver:(id<GMSTileReceiver>)receiver {
    NSString *path = self.pathTemplate;
    path = [path stringByReplacingOccurrencesOfString:@"{x}" withString:[NSString stringWithFormat: @"%ld", (long)x]];
    path = [path stringByReplacingOccurrencesOfString:@"{y}" withString:[NSString stringWithFormat: @"%ld", (long)(1 << zoom) - 1 - y]];
    path = [path stringByReplacingOccurrencesOfString:@"{z}" withString:[NSString stringWithFormat: @"%ld", (long)zoom]];
    NSURL *filePath = [NSURL URLWithString:path];
    if ([[NSFileManager defaultManager] fileExistsAtPath:filePath.path]) {
        UIImage* image = [UIImage imageWithData:[NSData dataWithContentsOfFile:filePath.path]];
        [receiver receiveTileWithX:x y:y zoom:zoom image:image];
    }
}

@end
