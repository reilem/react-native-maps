//
//  AIRGoogleMapRealLocalTile.h
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import <Foundation/Foundation.h>
#import <GoogleMaps/GoogleMaps.h>

@interface AIRGoogleMapLocalTileOverlay : GMSTileLayer

@property (nonatomic, strong) NSString *pathTemplate;

@end
