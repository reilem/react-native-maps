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
    NSURL *filePath = [self urlPathForZ:zoom x:x y:y];
    if ([[NSFileManager defaultManager] fileExistsAtPath:filePath.path]) {
        [receiver receiveTileWithX:x y:y zoom:zoom image:[UIImage imageWithData:[NSData dataWithContentsOfFile:filePath.path]]];
    } else {
        [receiver receiveTileWithX:x y:y zoom:zoom image:kGMSTileLayerNoTile];
    }
}

- (NSURL *)urlPathForZ:(NSUInteger)z x:(NSUInteger)x y:(NSUInteger)y {
    NSString *path = self.pathTemplate;
    path = [path stringByReplacingOccurrencesOfString:@"{x}" withString:[NSString stringWithFormat: @"%ld", (long)x]];
    path = [path stringByReplacingOccurrencesOfString:@"{y}" withString:[NSString stringWithFormat: @"%ld", (long)(1 << z) - 1 - y]];
    path = [path stringByReplacingOccurrencesOfString:@"{z}" withString:[NSString stringWithFormat: @"%ld", (long)z]];
    return [NSURL URLWithString:path];
}

@end
