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
    _tileLayer = [AIRGoogleMapLocalTileOverlay new];
    _tileLayer.pathTemplate = pathTemplate;
}

@end
