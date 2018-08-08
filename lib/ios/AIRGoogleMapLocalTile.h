//
//  AIRGoogleMapLocalTile.h
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import <Foundation/Foundation.h>
#import <GoogleMaps/GoogleMaps.h>
#import <AIRGoogleMapLocalTileOverlay.h>
#import <React/RCTViewManager.h>

@interface AIRGoogleMapLocalTile : UIView

@property (nonatomic, strong) GMSTileLayer *tileLayer;
@property (nonatomic, weak) NSString *fileTemplate;
@property (nonatomic, weak) NSString *urlTemplate;
@property (nonatomic, weak) NSArray *tempRange;
@property (nonatomic, weak) NSArray *currentTempRange;

@end
