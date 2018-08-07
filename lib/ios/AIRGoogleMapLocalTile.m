//
//  AIRGoogleMapLocalTile.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import "AIRGoogleMapLocalTile.h"

@implementation AIRGoogleMapLocalTile

- (void)setPathTemplate:(NSString *)pathTemplate {
    _pathTemplate = pathTemplate;
    GMSMapView *map = _tileLayer.map;
    _tileLayer.map = nil;
    _tileLayer = [AIRGoogleMapLocalTileOverlay new];
    _tileLayer.map = map;
    _tileLayer.pathTemplate = pathTemplate;
}

@end
