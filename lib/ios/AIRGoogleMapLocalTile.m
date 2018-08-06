//
//  AIRGoogleMapLocalTile.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import "AIRGoogleMapLocalTile.h"

@implementation AIRGoogleMapLocalTile

- (void)setPathTemplate:(NSString *)pathTemplate {
    self.pathTemplate = pathTemplate;
    self.tileLayer = [AIRGoogleMapRealLocalTile new];
}

@end
