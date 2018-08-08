//
//  AIRGoogleMapLocalTileManager.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import "AIRGoogleMapLocalTileManager.h"
#import "AIRGoogleMapLocalTile.h"

@implementation AIRGoogleMapLocalTileManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
    AIRGoogleMapLocalTile *tileLayer = [AIRGoogleMapLocalTile new];
    return tileLayer;
}

RCT_EXPORT_VIEW_PROPERTY(fileTemplate, NSString)

RCT_EXPORT_VIEW_PROPERTY(urlTemplate, NSString)

RCT_EXPORT_VIEW_PROPERTY(tempRange, NSArray)

@end
