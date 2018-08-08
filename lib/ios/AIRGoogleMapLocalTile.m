//
//  AIRGoogleMapLocalTile.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 06/08/2018.
//

#import "AIRGoogleMapLocalTile.h"
#import "AIRGoogleMapUrlTile.h"
#import "AIRGoogleMap.h"

@implementation AIRGoogleMapLocalTile

- (void)setFileTemplate:(NSString *)fileTemplate {
    _fileTemplate = fileTemplate;
    _tileLayer = [self _createLocalOverlay];
}

- (void)setUrlTemplate:(NSString *)urlTemplate {
    _urlTemplate = urlTemplate;

    if (!self.fileTemplate) {
        _tileLayer = [self _createUrlOverlay];
    } else if ((!self.fileTemplate && self.tempRange && self.currentTempRange) || self.fileTemplate) {
        _tileLayer = [self _createLocalOverlay];
    }
}

- (void)setTempRange:(NSArray *)tempRange {
    _tempRange = tempRange;
    if (self.tempRange && self.currentTempRange) {
        _tileLayer = [self _createLocalOverlay];
    }
}

- (void)setCurrentTempRange:(NSArray *)currentTempRange {
    _currentTempRange = currentTempRange;
    if (self.tempRange && self.currentTempRange) {
        _tileLayer = [self _createLocalOverlay];
    }
}

- (GMSURLTileLayer *)_createUrlOverlay
{
    GMSMapView *map = nil;
    if (self.tileLayer) map = self.tileLayer.map;
    self.tileLayer.map = nil;

    GMSURLTileLayer *newTileLayer = [GMSURLTileLayer tileLayerWithURLConstructor:[self _getTileURLConstructor]];
    if (map) newTileLayer.map = map;
    return newTileLayer;
}

- (AIRGoogleMapLocalTileOverlay *)_createLocalOverlay
{
    GMSMapView *map = nil;
    if (self.tileLayer) map = self.tileLayer.map;
    self.tileLayer.map = nil;

    AIRGoogleMapLocalTileOverlay *newTileLayer = [AIRGoogleMapLocalTileOverlay new];
    if (self.fileTemplate) newTileLayer.fileTemplate = self.fileTemplate;
    if (self.urlTemplate) newTileLayer.urlTemplate = self.urlTemplate;
    if (self.tempRange) newTileLayer.tempRange = self.tempRange;
    if (self.currentTempRange) newTileLayer.currentTempRange = self.currentTempRange;
    if (map) newTileLayer.map = map;
    return newTileLayer;
}

- (GMSTileURLConstructor)_getTileURLConstructor
{
  NSString *urlTemplate = self.urlTemplate;
  GMSTileURLConstructor urls = ^NSURL* _Nullable (NSUInteger x, NSUInteger y, NSUInteger zoom) {
    NSString *url = urlTemplate;
    url = [url stringByReplacingOccurrencesOfString:@"{x}" withString:[NSString stringWithFormat: @"%ld", (long)x]];
    url = [url stringByReplacingOccurrencesOfString:@"{y}" withString:[NSString stringWithFormat: @"%ld", (long)(1 << zoom) - 1 - y]];
    url = [url stringByReplacingOccurrencesOfString:@"{z}" withString:[NSString stringWithFormat: @"%ld", (long)zoom]];

    return [NSURL URLWithString:url];
  };
  return urls;
}

@end
