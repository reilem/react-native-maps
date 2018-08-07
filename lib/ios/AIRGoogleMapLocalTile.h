//
//  AIRGoogleMapLocalTile.h
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import <Foundation/Foundation.h>
#import <GoogleMaps/GoogleMaps.h>
#import <AIRGoogleMapLocalTileOverlay.h>

@interface AIRGoogleMapLocalTile : UIView

@property (nonatomic, strong) AIRGoogleMapLocalTileOverlay *tileLayer;

@property (nonatomic, strong) NSString *pathTemplate;

@end
