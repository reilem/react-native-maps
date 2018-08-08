//
//  AIRGoogleMapLocalTile.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import "AIRGoogleMapLocalTile.h"
#import "AIRGoogleMap.h"

@implementation AIRGoogleMapLocalTile

- (void)setFileTemplate:(NSString *)fileTemplate {
    _fileTemplate = fileTemplate;
    GMSMapView *map = _tileLayer.map;
    _tileLayer.map = nil;
    _tileLayer = [AIRGoogleMapLocalTileOverlay new];
    _tileLayer.map = map;
    _tileLayer.fileTemplate = fileTemplate;
}

- (void)setUrlTemplate:(NSString *)urlTemplate {
    if (!self.fileTemplate) {
        // Make url overlay
    } else if (self.tempRange) {
        // Make custom tile overlay
    } else {
        // Make custom tile overlay
    }
}

- (void)setTempRange:(NSArray *)tempRange {

}

- (void)setCurrentTempRange:(NSArray *)tempRange {

}

@end
